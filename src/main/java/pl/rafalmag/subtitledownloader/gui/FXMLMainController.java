package pl.rafalmag.subtitledownloader.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class FXMLMainController implements Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainController.class);

	private Window window;

	@FXML
	protected TabPane tabPane;

	@FXML
	protected ProgressBar progressBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTab("/MainSelectMovieFileTab.fxml");
		initTab("/MainSelectMovieTitleTab.fxml");
		initTab("/MainSelectMovieSubtitlesTab.fxml");
		initTab("/MainDownloadAndTestTab.fxml");
	}

	private void initTab(String resourceStr) {
		URL resource = getClass().getResource(resourceStr);
		try (InputStream openStream = resource.openStream()) {
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			tabPane.getTabs().add((Tab) fxmlLoader.load(openStream));
			((FXMLMainTab) fxmlLoader.getController()).setMainController(this);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	@FXML
	protected void openAbout() throws IOException {
		LOGGER.trace("openAbout");

		final Stage aboutStage = new Stage(StageStyle.UTILITY);
		aboutStage.initOwner(window);
		aboutStage.initModality(Modality.WINDOW_MODAL);

		aboutStage.setTitle("About Subtitles Downloader");

		URL resource = getClass().getResource("/About.fxml");
		Parent aboutView = FXMLLoader.load(resource);
		aboutStage.setScene(new Scene(aboutView));
		aboutStage.show();
	}

	@FXML
	protected void closeApp() {
		LOGGER.trace("closeApp");
		window.hide();
		LOGGER.trace("closeApp: hidden");
	}

}
