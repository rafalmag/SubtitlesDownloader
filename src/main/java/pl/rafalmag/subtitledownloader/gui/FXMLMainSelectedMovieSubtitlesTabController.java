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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;
import pl.rafalmag.subtitledownloader.subtitles.SubtitlesList;
import pl.rafalmag.subtitledownloader.title.MovieTitlesList;
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
				"Selected subtitles: ", SelectTitleProperties.getInstance()
						.selectedMovieProperty());
		selectedSubtitles.textProperty().bind(selectedSubtitlesText);

		setTable();

		// addUpdateTableListener();
	}

	private void addUpdateTableListener() {
		StringProperty movieFileProperty = SelectMovieProperties.getInstance()
				.movieFileProperty();

		StringProperty lastUpdatedForFilePathProperty = MovieTitlesList
				.lastUpdatedForFilePathProperty();

		BooleanBinding movieFilePathChangedBinding = Bindings.notEqual(
				movieFileProperty, lastUpdatedForFilePathProperty);
		ReadOnlyBooleanProperty tabSelectedProperty = selectMovieSubtitlesTab
				.selectedProperty();

		final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty
				.and(movieFilePathChangedBinding);

		InvalidationListener shouldUpdateTitlesListListener = new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				LOGGER.trace("observable: " + observable);
				if (shouldUpdateTitlesListBinding.get()) {
					try {
						MovieTitlesList.updateList(10000);
					} catch (SubtitlesDownloaderException
							| InterruptedException e) {
						LOGGER.error("Could not update titles list", e);
					}
				}

			}
		};

		tabSelectedProperty.addListener(shouldUpdateTitlesListListener);
		movieFileProperty.addListener(shouldUpdateTitlesListListener);
		lastUpdatedForFilePathProperty
				.addListener(shouldUpdateTitlesListListener);
	}

	private void setTable() {
		table.setItems(SubtitlesList.listProperty());

		TableColumn<Subtitles, String> fileName = new TableColumn<>("File Name");
		fileName.setCellValueFactory(new PropertyValueFactory<Subtitles, String>(
				"fileName"));
		fileName.setPrefWidth(500);
		TableColumn<Subtitles, Integer> downloadsCount = new TableColumn<>(
				"Downloads Count");
		downloadsCount.setCellValueFactory(new PropertyValueFactory<Subtitles, Integer>(
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
