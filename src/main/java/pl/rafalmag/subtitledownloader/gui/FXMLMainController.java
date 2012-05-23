package pl.rafalmag.subtitledownloader.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
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
	protected Tab selectMovieFileTab;
	@FXML
	protected Tab selectMovieTitleTab;
	@FXML
	protected Tab selectMovieSubtitlesTab;
	@FXML
	protected Tab testSubtitlesTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initSelectMovieFileTab();
		initSelectMovieTitleTab();
	}

	private void initSelectMovieTitleTab() {
		URL resource = getClass().getResource("/MainSelectMovieTitleTab.fxml");
		try (InputStream openStream = resource.openStream()) {
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			selectMovieTitleTab.setContent((Node) fxmlLoader.load(openStream));
			// ((FXMLMainSelectedMovieFileTabController) fxmlLoader
			// .getController()).setWindow(window);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private void initSelectMovieFileTab() {
		URL resource = getClass().getResource("/MainSelectMovieFileTab.fxml");
		try (InputStream openStream = resource.openStream()) {
			FXMLLoader fxmlLoader = new FXMLLoader(resource);
			selectMovieFileTab.setContent((Node) fxmlLoader.load(openStream));
			((FXMLMainSelectedMovieFileTabController) fxmlLoader
					.getController()).setWindow(window);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	@FXML
	protected void openAbout(ActionEvent event) throws IOException {
		LOGGER.trace("openAbout");

		final Stage aboutStage = new Stage(StageStyle.UTILITY);
		aboutStage.initOwner(window);
		aboutStage.initModality(Modality.WINDOW_MODAL);

		aboutStage.setTitle("About Subtitles Downloader");

		URL resource = getClass().getResource("/About.fxml");
		Parent aboutView = FXMLLoader.load(resource);
		aboutStage.setScene(new Scene(aboutView));
		aboutStage.show();
		event.consume();
	}

	@FXML
	protected void closeApp(ActionEvent event) {
		LOGGER.trace("closeApp");
		window.hide();
		LOGGER.trace("closeApp: hidden");
		event.consume();
	}

}
