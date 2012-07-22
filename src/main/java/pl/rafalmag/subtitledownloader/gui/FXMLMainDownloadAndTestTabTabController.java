package pl.rafalmag.subtitledownloader.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.subtitles.Downloader;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;

public class FXMLMainDownloadAndTestTabTabController extends FXMLMainTab {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainDownloadAndTestTabTabController.class);

	@FXML
	protected Label status;

	@FXML
	protected Button download;

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

		disabledDownloadProperty.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (!disabledDownloadProperty.get()) {
					download.tooltipProperty().set(
							new Tooltip("Download subtitles: "
									+ SelectSubtitlesProperties.getInstance()
											.selectedSubtitlesProperty().get()
											.getFileName()));
				}

			}
		});
	}

	private void initTestButton() {
		final BooleanBinding disabledTestProperty = SelectMovieProperties
				.getInstance().movieFileProperty().isEqualTo("");
		test.disableProperty().bind(disabledTestProperty);

		disabledTestProperty.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (!disabledTestProperty.get()) {
					test.tooltipProperty().set(
							new Tooltip("View movie: "
									+ SelectMovieProperties.getInstance()
											.getFilePath()));
				}

			}
		});
	}

	private void initMarkValid() {
		// TODO
		markValid.disableProperty().set(true);
	}

	@FXML
	protected void download() {
		fxmlMainController.progressBar.disableProperty().set(false);
		fxmlMainController.progressBar.setProgress(0);
		LOGGER.trace("download");
		Subtitles subtitles = SelectSubtitlesProperties.getInstance()
				.getSelectedSubtitles();
		File movieFile = SelectMovieProperties.getInstance().getFile();
		Downloader downloader = new Downloader(subtitles, movieFile);
		downloader.setProgressProperty(fxmlMainController.progressBar
				.progressProperty());
		try {
			downloader.download();
		} catch (SubtitlesDownloaderException e) {
			LOGGER.error(e.getMessage(), e);
		}
		fxmlMainController.progressBar.setProgress(1);
		fxmlMainController.progressBar.disableProperty().set(true);
	}

	@FXML
	protected void test() {
		LOGGER.trace("test");
		File file = SelectMovieProperties.getInstance().getFile();
		try {
			URI uri = file.toURI();
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			LOGGER.error(
					"Could not open URL " + file + " because of "
							+ e.getMessage(), e);
		}
	}

	@FXML
	protected void markValid() {
		LOGGER.trace("markValid");
		// TODO
		// http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#TryUploadSubtitles

	}
}
