package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class FXMLMainSelectedMovieTitleTabController implements Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieTitleTabController.class);

	// private Window window;

	@FXML
	protected Label selectedTitle;

	@FXML
	protected TableView<Movie> table;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		table.setItems(MovieTitlesList.getList());

		TableColumn<Movie, String> title = new TableColumn<>("Title");
		title.setCellValueFactory(new PropertyValueFactory<Movie, String>(
				"title"));
		title.setPrefWidth(500);
		TableColumn<Movie, Integer> year = new TableColumn<>("Year");
		year.setCellValueFactory(new PropertyValueFactory<Movie, Integer>(
				"year"));

		table.getColumns().setAll(ImmutableList.of(title, year));
	}
	// public void setWindow(Window window) {
	// this.window = window;
	// }

}
