package pl.rafalmag.subtitledownloader.title;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.japi.pf.PFBuilder;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbService;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Timeout;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Singleton
public class TitleService {

    @InjectLogger
    private Logger LOG;

    @Inject
    private CheckMovie checkMovie;

    @Inject
    private Session session;

    @Inject
    private TheMovieDbService theMovieDbService;

    @Inject
    private ActorSystem system;

    public Set<Movie> getTitles(File movieFile, long timeoutMs, ProgressCallback progressCallback)
            throws InterruptedException {
        String title = TitleNameUtils.getTitleFrom(movieFile.getName());
        return startTasksAndGetResults(title, movieFile, timeoutMs, progressCallback);
    }

    public static final ExecutionContextExecutor EXECUTOR = ExecutionContexts.fromExecutor(Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true).namingPattern("Title-%d").build()));

    private Set<Movie> startTasksAndGetResults(String title, File movieFile, long timeoutMs,
                                               ProgressCallback progressCallback)
            throws InterruptedException {

        Timeout timeout = new Timeout(timeoutMs, TimeUnit.MILLISECONDS);

        Source<Movie, NotUsed> source = Source.combine(getByTitle(title), getByFileHash(movieFile), Collections.singletonList(getByFileName(movieFile)), Merge::create)
                .takeWithin(Duration.of(timeoutMs, ChronoUnit.MILLIS))
                .map(x -> {
                    progressCallback.updateProgress(
                            timeout.getElapsedTime(TimeUnit.MILLISECONDS), timeoutMs);
                    return x;
                });
        Sink<Movie, CompletionStage<Set<Movie>>> sink = Sink.fold(new HashSet<>(), (set, elem) -> {
            set.add(elem);
            return set;
        });
        CompletionStage<Set<Movie>> listCompletionStage = source.runWith(sink, system);

        try {
            return listCompletionStage.toCompletableFuture().get(timeoutMs * 10, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SessionException) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText(cause.getMessage());
                    alert.showAndWait();
                });
            }
            LOG.error("Exception in task: " + cause.getMessage(), cause);
            return Collections.emptySet();
        } catch (TimeoutException e) {
            throw new IllegalStateException("Timeout while waiting for seq from stream", e);
        } finally {
            progressCallback.updateProgress(1);
        }
    }

    protected Source<Movie, NotUsed> getByFileName(File movieFile) {
        Future<Optional<Movie>> future = Futures.future(() -> session.guessMovieFromFileName(movieFile.getName()), EXECUTOR);
        return Source.fromFuture(future)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .recoverWith(new PFBuilder<Throwable, Source<Movie, NotUsed>>()
                        .match(Exception.class, ex -> {
                            LOG.error("Could not guess movie by file name " + movieFile.getName() + ", because of " + ex.getMessage(), ex);
                            return Source.empty();
                        })
                        .build());
    }

    protected Source<Movie, NotUsed> getByTitle(String title) {
        Future<List<MovieInfo>> future = Futures.future(() -> theMovieDbService.searchMovie(title), EXECUTOR);
        return Source.fromFuture(future).mapConcat(x -> x).map(Movie::new)
                .recoverWith(new PFBuilder<Throwable, Source<Movie, NotUsed>>()
                        .match(Exception.class, ex -> {
                            LOG.error("Could not retrieve movie by title " + title + " from theMovieDbService, because of " + ex.getMessage(), ex);
                            return Source.empty();
                        })
                        .build());
    }

    protected Source<Movie, NotUsed> getByFileHash(File movieFile) {
        Future<List<MovieEntity>> future = Futures.future(() -> checkMovie.getTitleInfo(movieFile), EXECUTOR);
        return Source.fromFuture(future).mapConcat(x -> x).map(Movie::new)
                .recoverWith(new PFBuilder<Throwable, Source<Movie, NotUsed>>()
                        .match(Exception.class, ex -> {
                            LOG.error("Could not get movie by file " + movieFile.getName() + " hash, because of " + ex.getMessage(), ex);
                            return Source.empty();
                        })
                        .build());
    }
}