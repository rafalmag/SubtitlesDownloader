package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.MovieTitlesList;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import com.google.common.collect.ImmutableList;

public class FXMLMainSelectedMovieTitleTabController implements Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieTitleTabController.class);

	@FXML
	protected Label selectedTitle;

	@FXML
	protected TableView<Movie> table;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringExpression selectedTitleText = Bindings.concat(
				"Selected title: ", SelectTitleProperties.getInstance()
						.selectedMovieProperty());
		selectedTitle.textProperty().bind(selectedTitleText);

		setTable();
	}

	private void setTable() {
		table.setItems(MovieTitlesList.getList());

		TableColumn<Movie, String> title = new TableColumn<>("Title");
		title.setCellValueFactory(new PropertyValueFactory<Movie, String>(
				"title"));
		title.setPrefWidth(500);
		TableColumn<Movie, Integer> year = new TableColumn<>("Year");
		year.setCellValueFactory(new PropertyValueFactory<Movie, Integer>(
				"year"));

		table.getColumns().setAll(ImmutableList.of(title, year));
		setSelectionStuff();
	}

	private void setSelectionStuff() {
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.getSelectionModel().getSelectedItems()
				.addListener(new InvalidationListener() {

					@Override
					public void invalidated(Observable observable) {
						LOGGER.trace("invalidated {}", observable);
						if (observable instanceof List<?>) {
							List<?> list = (List<?>) observable;
							if (!list.isEmpty()) {
								Object object = list.get(0);
								LOGGER.debug("invalidated with object: {}",
										object);
								if (object instanceof Movie) {
									Movie movie = (Movie) object;
									SelectTitleProperties.getInstance()
											.setSelectedMovie(movie);
								}
							}
						}

					}
				});
	}
}
