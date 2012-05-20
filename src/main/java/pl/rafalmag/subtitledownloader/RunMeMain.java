package pl.rafalmag.subtitledownloader;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.gui.FXMLMainController;

public class RunMeMain extends Application {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RunMeMain.class);

	public static void main(String[] args) {
		LOGGER.debug("SubtitlesDownloader app started");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LOGGER.trace("App started: start");

		primaryStage.setTitle("Subtitles Downloader");

		URL resource = getClass().getResource("/subtitlesDownloader.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		Parent root = (Parent) fxmlLoader.load(resource.openStream());
		FXMLMainController controller = (FXMLMainController) fxmlLoader
				.getController();
		controller.setStage(primaryStage);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

}
