package pl.rafalmag.subtitledownloader.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLMainSelectedMovieFileTabController extends FXMLMainTab {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieFileTabController.class);

	@FXML
	protected Tab selectMovieFileTab;

	@FXML
	protected Label selectedFile;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringExpression selectedMovieText = Bindings.concat(
				"Selected movie: ", SelectMovieProperties.getInstance()
						.movieFileProperty());
		selectedFile.textProperty().bind(selectedMovieText);
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

	@FXML
	protected void browseFile(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		File initialDir = SelectMovieProperties.getInstance().getInitialDir();
		LOGGER.trace("browseFile initialDir {}", initialDir);
		fileChooser.setInitialDirectory(initialDir);
		fileChooser.setTitle("Choose movie file");
		File file = fileChooser.showOpenDialog(fxmlMainController.getWindow());
		selectFile(file, true);
		event.consume();
	}

	private void selectFile(File file, boolean setInitialDir) {
		if (file != null) {
			SelectMovieProperties.getInstance().setFile(file);
			if (setInitialDir) {
				SelectMovieProperties.getInstance().setInitialDir(
						file.getParentFile());
			}
		}
	}

}
