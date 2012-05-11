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

	public synchronized void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	protected synchronized void openAbout(ActionEvent event) throws IOException {
		LOGGER.debug("openAbout");

		final Stage stage = new Stage(StageStyle.UTILITY);
		stage.initOwner(primaryStage);

		stage.setTitle("FXML TableView Example");

		URL resource = getClass().getResource("/subtitlesDownloaderAbout.fxml");
		Parent root = FXMLLoader.load(resource);
		stage.setScene(new Scene(root));
		stage.show();
	}

	@FXML
	protected synchronized void closeApp(ActionEvent event) {
		LOGGER.debug("closeApp");
		primaryStage.hide();
		LOGGER.debug("closeApp: hiden");
	}
}
