package pl.rafalmag.subtitledownloader.title;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbService;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public SortedSet<Movie> getTitles(File movieFile, long timeoutMs, ProgressCallback progressCallback) throws InterruptedException {
        String title = TitleNameUtils.getTitleFrom(movieFile.getName());
        return startTasksAndGetResults(title, movieFile, timeoutMs, progressCallback);
    }

    private final static ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true).namingPattern("Title-%d").build());

    private SortedSet<Movie> startTasksAndGetResults(String title, File movieFile, long timeoutMs, ProgressCallback progressCallback)
            throws InterruptedException {
        Collection<? extends Callable<List<Movie>>> solvers = ImmutableList.of(
                new NamedCallable<>("-movByTitle", () -> getByTitle(title)),
                new NamedCallable<>("-movByFileHash", () -> getByFileHash(movieFile))
        );
        Collection<List<Movie>> solve = Utils.solve(EXECUTOR, solvers, timeoutMs, progressCallback);

        return StreamSupport.stream(solve.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    protected List<Movie> getByTitle(String title) {
        List<MovieInfo> searchMovie = theMovieDbService.searchMovie(title);
        List<Movie> list = Lists.transform(searchMovie, Movie::new);
        LOG.debug("TheMovieDb returned: {}", list);
        return list;
    }

    protected List<Movie> getByFileHash(File movieFile) throws SubtitlesDownloaderException {
        session.login(); // mandatory

        List<MovieEntity> checkMovieHash2Entities = checkMovie.getTitleInfo(movieFile);

        return Lists.transform(checkMovieHash2Entities, Movie::new);
    }
}