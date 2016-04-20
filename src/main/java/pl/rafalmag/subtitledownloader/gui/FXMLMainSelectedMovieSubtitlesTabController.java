package pl.rafalmag.subtitledownloader.gui;

import com.google.common.collect.ImmutableList;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;
import pl.rafalmag.subtitledownloader.subtitles.SubtitlesList;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainSelectedMovieSubtitlesTabController extends FXMLMainTab {

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

        InvalidationListener shouldUpdateSubtitlesListListener = observable -> {
            LOGGER.trace("observable: " + observable);
            if (shouldUpdateTitlesListBinding.get()) {
                try {
                    SubtitlesList.updateList(
                            fxmlMainController.progressBar, 10000);
                    // clear table
                    SubtitlesList.listProperty().clear();
                    SelectSubtitlesProperties
                            .getInstance()
                            .setSelectedSubtitles(Subtitles.DUMMY_SUBTITLES);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not update subtitles list", e);
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
        fileName.setCellValueFactory(new PropertyValueFactory<>(
                "fileName"));
        fileName.setPrefWidth(500);
        TableColumn<Subtitles, Integer> downloadsCount = new TableColumn<>(
                "Downloads");
        downloadsCount
                .setCellValueFactory(new PropertyValueFactory<>(
                        "downloadsCount"));

        table.getColumns().setAll(ImmutableList.of(fileName, downloadsCount));
        setSelectionStuff();
    }

    private void setSelectionStuff() {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        table.getSelectionModel().getSelectedItems()
                .addListener((InvalidationListener) observable -> {
                    if (table.getSelectionModel().getSelectedItems().size() == 1) {
                        Subtitles subtitles = table.getSelectionModel()
                                .getSelectedItems().get(0);
                        Subtitles oldSubtitles = SelectSubtitlesProperties
                                .getInstance()
                                .getSelectedSubtitles();
                        LOGGER.debug("Selected subtitles: {} old: {}",
                                subtitles, oldSubtitles);
                        SelectSubtitlesProperties.getInstance()
                                .setSelectedSubtitles(subtitles);
                        if (oldSubtitles == subtitles) {
                            // item was double clicked
                            fxmlMainController.nextTab();
                        }
                    } else {
                        // SelectSubtitlesProperties.getInstance()
                        // .setSelectedSubtitles(
                        // Subtitles.DUMMY_SUBTITLES);
                    }
                });
    }
}
