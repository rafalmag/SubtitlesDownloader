package pl.rafalmag.subtitledownloader.title;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;
import pl.rafalmag.subtitledownloader.utils.TaskWithProgressCallback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class MovieTitlesList {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieTitlesList.class);

    @Inject
    private TitleService titleService;

    private StringProperty lastUpdatedForFilePath = new SimpleStringProperty("");

    private ObservableList<Movie> list = FXCollections.observableArrayList();

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("MovieTitlesUpdate-%d")
                    .build());

    public ObservableList<Movie> listProperty() {
        return list;
    }

    public StringProperty lastUpdatedForFilePathProperty() {
        return lastUpdatedForFilePath;
    }

    public void updateList(final ProgressBar progressBar, final long timeoutMs) throws InterruptedException {

        LOGGER.debug("updateList timeoutMs=" + timeoutMs);
        final File file = SelectMovieProperties.getInstance().getFile();
        progressBar.disableProperty().set(false);

        TaskWithProgressCallback<Void> task = new TaskWithProgressCallback<Void>() {

            @Override
            protected Void call() throws Exception {
                final SortedSet<Movie> titles = titleService.getTitles(file, timeoutMs, this);
                Platform.runLater(() -> {
                    list.setAll(titles);
                    lastUpdatedForFilePath.setValue(SelectMovieProperties
                            .getInstance()
                            .getFilePath());
                    progressBar.disableProperty().set(true);
                });
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        EXECUTOR.submit(task);
    }
}
