package pl.rafalmag.subtitledownloader.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLMainController implements Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainController.class);

	private Stage primaryStage;

	@FXML
	protected Label selectedFile;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringExpression stringExpression = Bindings.concat("Selected movie: ",
				SelectMovieProperties.getInstance().movieFileProperty());
		selectedFile.textProperty().bind(stringExpression);
	}

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	protected void openAbout(ActionEvent event) throws IOException {
		LOGGER.trace("openAbout");

		final Stage aboutStage = new Stage(StageStyle.UTILITY);
		aboutStage.initOwner(primaryStage);

		aboutStage.setTitle("About Subtitles Downloader");

		URL resource = getClass().getResource("/subtitlesDownloaderAbout.fxml");
		Parent aboutView = FXMLLoader.load(resource);
		aboutStage.setScene(new Scene(aboutView));
		aboutStage.show();
		event.consume();
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
			selectFile(droppedFile);
		}
		event.consume();
	}

	private boolean isOneFileDragged(Dragboard dragboard) {
		return dragboard.hasFiles() && dragboard.getFiles().size() == 1;
	}

	@FXML
	protected void closeApp(ActionEvent event) {
		LOGGER.trace("closeApp");
		primaryStage.hide();
		LOGGER.trace("closeApp: hidden");
		event.consume();
	}

	@FXML
	protected void browseFile(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(SelectMovieProperties.getInstance()
				.getInitialDir());
		fileChooser.setTitle("Choose movie file");
		File file = fileChooser.showOpenDialog(primaryStage);
		selectFile(file);
		event.consume();
	}

	private void selectFile(File file) {
		if (file != null) {
			SelectMovieProperties.getInstance().setFile(file);
			SelectMovieProperties.getInstance().setInitialDir(
					file.getParentFile());
		}
	}
}
