package pl.rafalmag.subtitledownloader.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.subtitles.*;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class FXMLMainDownloadAndTestTabTabController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(FXMLMainDownloadAndTestTabTabController.class);

    private static final ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("DownloadAndTestTab#%d")
                    .uncaughtExceptionHandler((t, e) -> LOG.error("Unhandled exception: " + e.getMessage(), e))
                    .build());

    @FXML
    protected Label status;

    @FXML
    protected Button download;

    @FXML
    protected Button downloadAndtest;

    @FXML
    protected Button test;

    @FXML
    protected Button markValid;

    @Inject
    protected FXMLMainController fxmlMainController;

    @Inject
    private SelectMovieProperties selectMovieProperties;

    @Inject
    private SelectSubtitlesProperties selectSubtitlesProperties;

    @Inject
    private SelectTitleProperties selectTitleProperties;

    @Inject
    private SubtitlesService subtitlesService;

    // FIXME temp hack
    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        initDownloadButton();
        initTestButton();
        initMarkValid();
    }

    private void initDownloadButton() {
        final BooleanBinding disabledDownloadProperty = selectSubtitlesProperties.selectedSubtitlesProperty()
                .isEqualTo(Subtitles.DUMMY_SUBTITLES);
        download.disableProperty().bind(disabledDownloadProperty);
        downloadAndtest.disableProperty().bind(disabledDownloadProperty);

        disabledDownloadProperty.addListener(observable -> {
            if (!disabledDownloadProperty.get()) {
                download.tooltipProperty().set(
                        new Tooltip(resources.getString("DownloadSubtitles") + " "
                                + selectSubtitlesProperties.selectedSubtitlesProperty().get().getFileName()));
            }
        });
    }

    private void initTestButton() {
        final BooleanBinding disabledTestProperty = selectMovieProperties.movieFileProperty()
                .isEqualTo(SelectMovieProperties.NO_MOVIE_SELECTED);
        test.disableProperty().bind(disabledTestProperty);

        disabledTestProperty.addListener(observable -> {
            if (!disabledTestProperty.get()) {
                test.tooltipProperty().set(new Tooltip(resources.getString("ViewMovie") + " "
                        + selectMovieProperties.getFilePath()));
            }
        });
    }

    private void initMarkValid() {
        markValid.disableProperty().bind(selectTitleProperties.selectedMovieProperty().isEqualTo(Movie.DUMMY_MOVIE));
    }

    @FXML
    protected void download() {
        fxmlMainController.progressBar.disableProperty().set(false);
        LOG.trace("download");
        Subtitles subtitles = selectSubtitlesProperties.getSelectedSubtitles();
        File movieFile = selectMovieProperties.getFile();
        DownloaderTask downloader = new DownloaderTask(subtitles, movieFile,
                fxmlMainController.progressBar.disableProperty());
        fxmlMainController.progressBar.progressProperty().bind(downloader.progressProperty());

        EXECUTOR.submit(downloader);
    }

    @FXML
    protected void test() {
        LOG.trace("test");
        File file = selectMovieProperties.getFile();
        try {
            URI uri = file.toURI();
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            LOG.error("Could not open URL " + file + ", because of " + e.getMessage(), e);
        }
    }

    @FXML
    protected void markValid() {
        LOG.trace("markValid");
        if (subtitlesDownloaderProperties.getLoginAndPassword().equals(SubtitlesDownloaderProperties.ANONYMOUS)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please login before using this button");
            alert.showAndWait();
        }
        File movieFile = selectMovieProperties.getFile();
        UploadSubtitlesTask uploadSubtitles = new UploadSubtitlesTask(subtitlesService, selectTitleProperties.getSelectedMovie(), movieFile,
                fxmlMainController.progressBar.disableProperty());
        fxmlMainController.progressBar.progressProperty().bind(uploadSubtitles.progressProperty());

        EXECUTOR.submit(uploadSubtitles);
    }

    @FXML
    public void downloadAndTest() {
        download();
        test();
    }
}
