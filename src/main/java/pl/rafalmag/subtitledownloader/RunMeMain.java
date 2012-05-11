package pl.rafalmag.subtitledownloader;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.rafalmag.subtitledownloader.gui.FXMLMainController;

public class RunMeMain extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("FXML TableView Example");

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
