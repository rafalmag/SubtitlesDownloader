package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLMainSelectedMovieTitleTabController implements Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieTitleTabController.class);

	// private Window window;

	@FXML
	Label selectedTitle;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// StringExpression selectedMovieText = Bindings.concat(
		// "Selected movie: ", SelectMovieProperties.getInstance()
		// .movieFileProperty());
		// selectedTitle.textProperty().bind(selectedMovieText);
	}

	// public void setWindow(Window window) {
	// this.window = window;
	// }

}
