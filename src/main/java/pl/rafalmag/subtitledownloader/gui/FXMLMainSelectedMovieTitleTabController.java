package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.ResourceBundle;

import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.MovieTitlesList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import com.google.common.collect.ImmutableList;

public class FXMLMainSelectedMovieTitleTabController implements Initializable {

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

}
