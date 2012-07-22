package pl.rafalmag.subtitledownloader.gui;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initDownloadButton();
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

	@FXML
	protected void download() {
		LOGGER.trace("downlaod");
	}

	@FXML
	protected void test() {
		LOGGER.trace("test");
	}
}
