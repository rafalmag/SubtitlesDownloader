package pl.rafalmag.subtitledownloader.gui;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;
import pl.rafalmag.subtitledownloader.subtitles.SubtitlesListService;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

@Singleton
public class FXMLMainSelectedMovieSubtitlesTabController implements Initializable {

    @InjectLogger
    private Logger LOG;

    @FXML
    protected Tab selectMovieSubtitlesTab;

    @FXML
    protected Label selectedSubtitles;

    @FXML
    protected TableView<Subtitles> table;

    @Inject
    protected FXMLMainController fxmlMainController;

    @Inject
    private SubtitlesListService subtitlesListService;

    @Inject
    private SelectTitleProperties selectTitleProperties;

    @Inject
    private SelectSubtitlesProperties selectSubtitlesProperties;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        StringExpression selectedSubtitlesText = Bindings.concat(
                resources.getString("SelectedSubtitle"), " ", selectSubtitlesProperties.selectedSubtitlesProperty());
        selectedSubtitles.textProperty().bind(selectedSubtitlesText);

        setTable();

        addUpdateTableListener();
    }

    private void addUpdateTableListener() {
        ObjectProperty<Movie> selectedMovieProperty = selectTitleProperties.selectedMovieProperty();

        ObjectProperty<Movie> lastUpdatedForMovieProperty = subtitlesListService.lastUpdatedForMovieProperty();

        BooleanBinding selectedMovieChangedBinding =
                Bindings.notEqual(selectedMovieProperty, lastUpdatedForMovieProperty);
        ReadOnlyBooleanProperty tabSelectedProperty = selectMovieSubtitlesTab.selectedProperty();

        final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty.and(selectedMovieChangedBinding);

        InvalidationListener shouldUpdateSubtitlesListListener = observable -> {
            LOG.trace("observable: " + observable);
            if (shouldUpdateTitlesListBinding.get()) {
                try {
                    subtitlesListService.updateList(fxmlMainController.progressBar, 10000);
                    // clear table
                    subtitlesListService.listProperty().clear();
                    selectSubtitlesProperties.setSelectedSubtitles(Subtitles.DUMMY_SUBTITLES);
                } catch (InterruptedException e) {
                    LOG.error("Could not update subtitles list", e);
                }
            }

        };

        tabSelectedProperty.addListener(shouldUpdateSubtitlesListListener);
        selectedMovieProperty.addListener(shouldUpdateSubtitlesListListener);
        lastUpdatedForMovieProperty.addListener(shouldUpdateSubtitlesListListener);
    }

    private void setTable() {
        table.setItems(subtitlesListService.listProperty());
        table.setPlaceholder(new Label(resources.getString("NoContentInTable")));

        TableColumn<Subtitles, String> fileName = new TableColumn<>(resources.getString("FileName"));
        fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileName.setPrefWidth(500);

        TableColumn<Subtitles, Integer> downloadsCount = new TableColumn<>(resources.getString("Downloads"));
        downloadsCount.setCellValueFactory(new PropertyValueFactory<>("downloadsCount"));

        TableColumn<Subtitles, String> source = new TableColumn<>(resources.getString("Source"));
        source.setCellValueFactory(new PropertyValueFactory<>("source"));

        table.getColumns().setAll(ImmutableList.of(fileName, downloadsCount, source));
        setSelectionStuff();
    }

    private void setSelectionStuff() {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Subtitles selectedSubtitles = selectSubtitlesProperties.getSelectedSubtitles();
        if (selectedSubtitles != Subtitles.DUMMY_SUBTITLES) {
            table.getSelectionModel().select(selectedSubtitles);
        }

        table.getSelectionModel().getSelectedItems()
                .addListener((InvalidationListener) observable -> {
                    if (table.getSelectionModel().getSelectedItems().size() == 1) {
                        Subtitles subtitles = table.getSelectionModel().getSelectedItems().get(0);
                        Subtitles oldSubtitles = selectSubtitlesProperties.getSelectedSubtitles();
                        LOG.debug("Selected subtitles: {} old: {}", subtitles, oldSubtitles);
                        selectSubtitlesProperties.setSelectedSubtitles(subtitles);
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

        //double click on row navigates to next tab
        table.setRowFactory(tv -> {
            TableRow<Subtitles> row = new TableRow<Subtitles>() {
                @Override
                protected void updateItem(Subtitles subtitles, boolean empty) {
                    super.updateItem(subtitles, empty);
                    if(subtitles == null){
                        return;
                    }
                    if (subtitles.getSource().equalsIgnoreCase("hash")) {
                        if (!getStyleClass().contains("bold")) {
                            getStyleClass().add("bold");
                        }
                    } else {
                        getStyleClass().removeAll(Collections.singleton("bold"));
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Subtitles rowData = row.getItem();
                    LOG.trace("Double clicked {}", rowData);
                    fxmlMainController.nextTab();
                }
            });
            return row;
        });
    }

    @FXML
    protected void refreshTable() {
        LOG.trace("refresh");
        try {
            subtitlesListService.updateList(fxmlMainController.progressBar, 10000);
        } catch (InterruptedException e) {
            LOG.error("Could not update subtitles list", e);
        }
    }
}
