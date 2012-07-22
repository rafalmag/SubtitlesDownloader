package pl.rafalmag.subtitledownloader.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;
import pl.rafalmag.subtitledownloader.subtitles.SubtitlesList;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import com.google.common.collect.ImmutableList;

public class FXMLMainSelectedMovieSubtitlesTabController implements
		Initializable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLMainSelectedMovieSubtitlesTabController.class);

	@FXML
	protected Tab selectMovieSubtitlesTab;

	@FXML
	protected Label selectedSubtitles;

	@FXML
	protected TableView<Subtitles> table;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringExpression selectedSubtitlesText = Bindings.concat(
				"Selected subtitles: ", SelectSubtitlesProperties.getInstance()
						.selectedSubtitlesProperty());
		selectedSubtitles.textProperty().bind(selectedSubtitlesText);

		setTable();

		addUpdateTableListener();
	}

	private void addUpdateTableListener() {
		ObjectProperty<Movie> selectedMovieProperty = SelectTitleProperties
				.getInstance().selectedMovieProperty();

		ObjectProperty<Movie> lastUpdatedForMovieProperty = SubtitlesList
				.lastUpdatedForMovieProperty();

		BooleanBinding selectedMovieChangedBinding = Bindings.notEqual(
				selectedMovieProperty, lastUpdatedForMovieProperty);
		ReadOnlyBooleanProperty tabSelectedProperty = selectMovieSubtitlesTab
				.selectedProperty();

		final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty
				.and(selectedMovieChangedBinding);

		InvalidationListener shouldUpdateSubtitlesListListener = new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				LOGGER.trace("observable: " + observable);
				if (shouldUpdateTitlesListBinding.get()) {
					try {
						SubtitlesList.updateList(10000);
					} catch (InterruptedException e) {
						LOGGER.error("Could not update subtitles list", e);
					}
				}

			}
		};

		tabSelectedProperty.addListener(shouldUpdateSubtitlesListListener);
		selectedMovieProperty.addListener(shouldUpdateSubtitlesListListener);
		lastUpdatedForMovieProperty
				.addListener(shouldUpdateSubtitlesListListener);
	}

	private void setTable() {
		table.setItems(SubtitlesList.listProperty());

		TableColumn<Subtitles, String> fileName = new TableColumn<>("File Name");
		fileName.setCellValueFactory(new PropertyValueFactory<Subtitles, String>(
				"fileName"));
		fileName.setPrefWidth(500);
		TableColumn<Subtitles, Integer> downloadsCount = new TableColumn<>(
				"Downloads");
		downloadsCount
				.setCellValueFactory(new PropertyValueFactory<Subtitles, Integer>(
						"downloadsCount"));

		table.getColumns().setAll(ImmutableList.of(fileName, downloadsCount));
		setSelectionStuff();
	}

	private void setSelectionStuff() {
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		table.getSelectionModel().getSelectedItems()
				.addListener(new InvalidationListener() {

					@Override
					public void invalidated(Observable observable) {
						if (table.getSelectionModel().getSelectedItems().size() == 1) {
							Subtitles subtitles = table.getSelectionModel()
									.getSelectedItems().get(0);
							LOGGER.debug("Selected subtitles: {}", subtitles);
							SelectSubtitlesProperties.getInstance()
									.setSelectedSubtitles(subtitles);
						}
					}
				});
	}
}