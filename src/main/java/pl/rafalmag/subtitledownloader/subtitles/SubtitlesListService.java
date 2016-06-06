package pl.rafalmag.subtitledownloader.subtitles;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;
import pl.rafalmag.subtitledownloader.utils.TaskWithProgressCallback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class SubtitlesListService {

    @InjectLogger
    private Logger LOG;

    @Inject
    private SubtitlesService subtitlesService;

    @Inject
    private SelectTitleProperties selectTitleProperties;

    @Inject
    private SelectMovieProperties selectMovieProperties;

    private final ObservableList<Subtitles> list = FXCollections.observableArrayList();

    private final ObjectProperty<Movie> lastUpdatedForMovie = new SimpleObjectProperty<>(Movie.DUMMY_MOVIE);

    public ObservableList<Subtitles> listProperty() {
        return list;
    }

    public ObjectProperty<Movie> lastUpdatedForMovieProperty() {
        return lastUpdatedForMovie;
    }

    private static final ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("SubtitlesUpdate-%d")
                    .build());

    public void updateList(final ProgressBar progressBar, final long timeoutMs) throws InterruptedException {
        LOG.debug("SubtitlesListService update timeout " + timeoutMs + "ms");
        progressBar.disableProperty().set(false);
        final Movie selectedMovie = selectTitleProperties.getSelectedMovie();
        final File selectedFile = selectMovieProperties.getFile();
        TaskWithProgressCallback<Void> task = new TaskWithProgressCallback<Void>() {

            @Override
            protected Void call() throws Exception {
                final SortedSet<Subtitles> subtitles = subtitlesService.getSubtitles(
                        selectedMovie,
                        selectedFile, timeoutMs, this);
                Platform.runLater(() -> {
                    list.setAll(subtitles);
                    lastUpdatedForMovie.setValue(selectedMovie);
                    progressBar.disableProperty().set(true);
                });
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        EXECUTOR.submit(task);
    }

}
