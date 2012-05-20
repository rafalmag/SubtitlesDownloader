package pl.rafalmag.subtitledownloader.gui;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLMainController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainController.class);

	private Stage primaryStage;

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	protected void openAbout(ActionEvent event) throws IOException {
		LOGGER.trace("FXMLMainController: openAbout");

		final Stage aboutStage = new Stage(StageStyle.UTILITY);
		aboutStage.initOwner(primaryStage);

		aboutStage.setTitle("About Subtitles Downloader");

		URL resource = getClass().getResource("/subtitlesDownloaderAbout.fxml");
		Parent aboutView = FXMLLoader.load(resource);
		aboutStage.setScene(new Scene(aboutView));
		aboutStage.show();
	}

	@FXML
	protected void closeApp(ActionEvent event) {
		LOGGER.trace("closeApp");
		primaryStage.hide();
		LOGGER.trace("closeApp: hidden");
	}
}
