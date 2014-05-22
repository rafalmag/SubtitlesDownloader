package pl.rafalmag.subtitledownloader.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

	@FXML
	protected Button previousButton;

	@FXML
	protected Button nextButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTab("/MainSelectMovieFileTab.fxml");
		initTab("/MainSelectMovieTitleTab.fxml");
		initTab("/MainSelectMovieSubtitlesTab.fxml");
		initTab("/MainDownloadAndTestTab.fxml");
		progressBar.disableProperty().set(true);

		previousButton.disableProperty().bind(
				tabPane.getTabs().get(0).selectedProperty());
		nextButton.disableProperty().bind(
				tabPane.getTabs().get(tabPane.getTabs().size() - 1)
						.selectedProperty());
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

	@FXML
	protected void nextTab() {
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
		tabPane.getSelectionModel().select(selectedIndex + 1);
	}

	@FXML
	protected void previousTab() {
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
		tabPane.getSelectionModel().select(selectedIndex - 1);
	}

	@FXML
	protected void setOnDragOver(DragEvent event) {
		// LOGGER.trace("setOnDragOver {}", event);
		Dragboard dragboard = event.getDragboard();
		if (isOneFileDragged(dragboard)) {
			event.acceptTransferModes(TransferMode.LINK);
		}
		event.consume();
	}

	@FXML
	protected void setOnDragEntered(DragEvent event) {
		LOGGER.trace("setOnDragEntered {}", event);
		Dragboard dragboard = event.getDragboard();
		if (isOneFileDragged(dragboard)) {
			event.acceptTransferModes(TransferMode.LINK);
		}
		event.consume();
	}

	@FXML
	protected void setOnDragExited(DragEvent event) {
		LOGGER.trace("setOnDragExited {}", event);
		event.consume();
	}

	@FXML
	protected void setOnDragDropped(DragEvent event) {
		LOGGER.trace("setOnDragDropped {}", event);
		Dragboard dragboard = event.getDragboard();
		if (isOneFileDragged(dragboard)) {
			File droppedFile = dragboard.getFiles().get(0);
			LOGGER.debug("setOnDragDropped file: {}", droppedFile);
			selectFile(droppedFile, false);
		}
		event.consume();
	}

	private boolean isOneFileDragged(Dragboard dragboard) {
		return dragboard.hasFiles() && dragboard.getFiles().size() == 1;
	}

	void selectFile(File file, boolean setInitialDir) {
		if (file != null) {
			SelectMovieProperties.getInstance().setFile(file);
			if (setInitialDir) {
				SelectMovieProperties.getInstance().setInitialDir(
						file.getParentFile());
			}
			openTitleTab();
		}
	}

	private void openTitleTab() {
		tabPane.getSelectionModel().select(1);
	}

}
