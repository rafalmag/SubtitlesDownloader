package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.MovieTitlesList;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import com.google.common.collect.ImmutableList;

public class FXMLMainSelectedMovieTitleTabController extends FXMLMainTab {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieTitleTabController.class);

	@FXML
	protected Tab selectMovieTitleTab;

	@FXML
	protected Label selectedTitle;

	@FXML
	protected TableView<Movie> table;

	// TODO add listener for refreshButton listen on
	// SelectMovieProperties.getInstance().getFile();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringExpression selectedTitleText = Bindings.concat(
				"Selected title: ", SelectTitleProperties.getInstance()
						.selectedMovieProperty());
		selectedTitle.textProperty().bind(selectedTitleText);

		setTable();

		addUpdateTableListener();
	}

	private void addUpdateTableListener() {
		StringProperty movieFileProperty = SelectMovieProperties.getInstance()
				.movieFileProperty();

		StringProperty lastUpdatedForFilePathProperty = MovieTitlesList
				.lastUpdatedForFilePathProperty();

		BooleanBinding movieFilePathChangedBinding = Bindings.notEqual(
				movieFileProperty, lastUpdatedForFilePathProperty);
		ReadOnlyBooleanProperty tabSelectedProperty = selectMovieTitleTab
				.selectedProperty();

		final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty
				.and(movieFilePathChangedBinding);

		InvalidationListener shouldUpdateTitlesListListener = new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				LOGGER.trace("observable: " + observable);
				if (shouldUpdateTitlesListBinding.get()) {
					refreshTable();

					// clear table
					MovieTitlesList.listProperty().clear();

					SelectTitleProperties
							.getInstance()
							.setSelectedMovie(Movie.DUMMY_MOVIE);

				}

			}
		};

		tabSelectedProperty.addListener(shouldUpdateTitlesListListener);
		movieFileProperty.addListener(shouldUpdateTitlesListListener);
		lastUpdatedForFilePathProperty
				.addListener(shouldUpdateTitlesListListener);
	}

	private void setTable() {
		table.setItems(MovieTitlesList.listProperty());

		TableColumn<Movie, String> title = new TableColumn<>("Title");
		title.setCellValueFactory(new PropertyValueFactory<Movie, String>(
				"title"));
		title.setPrefWidth(500);
		TableColumn<Movie, Integer> year = new TableColumn<>("Year");
		year.setCellValueFactory(new PropertyValueFactory<Movie, Integer>(
				"year"));
		// year.setCellFactory(new Callback<TableColumn<Movie, Integer>,
		// TableCell<Movie, Integer>>() {
		//
		// @Override
		// public TableCell<Movie, Integer> call(
		// TableColumn<Movie, Integer> param) {
		// final TableCell<Movie, Integer> cell = new TableCell<>();
		// cell.setOnMouseClicked(new EventHandler<Event>() {
		//
		// @Override
		// public void handle(Event event) {
		// LOGGER.info(cell.getText() + " # " + event.toString());
		//
		// }
		// });
		// return cell;
		//
		// }
		// });

		table.getColumns().setAll(ImmutableList.of(title, year));
		setSelectionStuff();
	}

	private void setSelectionStuff() {
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		// SelectTitleProperties.getInstance().selectedMovieProperty()
		// .bind(table.getSelectionModel().selectedItemProperty());

		table.getSelectionModel().getSelectedItems()
				.addListener(new InvalidationListener() {

					@Override
					public void invalidated(Observable observable) {
						if (table.getSelectionModel().getSelectedItems().size() == 1) {
							Movie movie = table.getSelectionModel()
									.getSelectedItems().get(0);
							Movie oldSelectedMovie = SelectTitleProperties
									.getInstance()
									.getSelectedMovie();
							LOGGER.debug("Selected movie: {}, old: {}", movie,
									oldSelectedMovie);

							SelectTitleProperties.getInstance()
									.setSelectedMovie(movie);
							if (oldSelectedMovie == movie) {
								// item was double clicked
								fxmlMainController.nextTab();
							}
						} else {
							// SelectTitleProperties.getInstance()
							// .setSelectedMovie(Movie.DUMMY_MOVIE);
						}
					}
				});
	}

	@FXML
	protected void refreshTable() {
		LOGGER.trace("refresh");
		try {
			MovieTitlesList.updateList(fxmlMainController.progressBar, 10000);
		} catch (InterruptedException e) {
			LOGGER.error("Could not update titles list", e);
		}
	}
}
