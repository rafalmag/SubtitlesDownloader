package pl.rafalmag.subtitledownloader.gui;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.subtitles.DownloaderTask;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FXMLMainDownloadAndTestTabTabController extends FXMLMainTab {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FXMLMainDownloadAndTestTabTabController.class);

    private static final ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("DownloadTask#%d")
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDownloadButton();
        initTestButton();
        initMarkValid();
    }

    private void initDownloadButton() {
        final BooleanBinding disabledDownloadProperty = SelectSubtitlesProperties
                .getInstance().selectedSubtitlesProperty()
                .isEqualTo(Subtitles.DUMMY_SUBTITLES);
        download.disableProperty().bind(disabledDownloadProperty);
        downloadAndtest.disableProperty().bind(disabledDownloadProperty);

        disabledDownloadProperty.addListener(observable -> {
            if (!disabledDownloadProperty.get()) {
                download.tooltipProperty().set(
                        new Tooltip("Download subtitles: "
                                + SelectSubtitlesProperties.getInstance()
                                .selectedSubtitlesProperty().get()
                                .getFileName()));
            }

        });

    }

    private void initTestButton() {
        final BooleanBinding disabledTestProperty = SelectMovieProperties
                .getInstance().movieFileProperty()
                .isEqualTo(SelectMovieProperties.NO_MOVIE_SELECTED);
        test.disableProperty().bind(disabledTestProperty);

        disabledTestProperty.addListener(observable -> {
            if (!disabledTestProperty.get()) {
                // TODO localization
                test.tooltipProperty().set(new Tooltip("View movie: " + SelectMovieProperties.getInstance().getFilePath()));
            }

        });
    }

    private void initMarkValid() {
        // TODO mark valid
        markValid.disableProperty().set(true);
    }

    @FXML
    protected void download() {
        fxmlMainController.progressBar.disableProperty().set(false);
        LOGGER.trace("download");
        Subtitles subtitles = SelectSubtitlesProperties.getInstance().getSelectedSubtitles();
        File movieFile = SelectMovieProperties.getInstance().getFile();
        DownloaderTask downloader = new DownloaderTask(subtitles, movieFile,
                fxmlMainController.progressBar.disableProperty());
        fxmlMainController.progressBar.progressProperty().bind(downloader.progressProperty());

        EXECUTOR.submit(downloader);
    }

    @FXML
    protected void test() {
        LOGGER.trace("test");
        File file = SelectMovieProperties.getInstance().getFile();
        try {
            URI uri = file.toURI();
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            LOGGER.error("Could not open URL " + file + ", because of " + e.getMessage(), e);
        }
    }

    @FXML
    protected void markValid() {
        LOGGER.trace("markValid");
        // TODO
        // http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#TryUploadSubtitles
    }

    @FXML
    public void downloadAndTest() {
        download();
        test();
    }
}
