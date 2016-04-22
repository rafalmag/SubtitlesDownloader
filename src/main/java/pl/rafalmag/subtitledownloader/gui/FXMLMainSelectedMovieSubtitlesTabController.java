package pl.rafalmag.subtitledownloader.gui;

import com.google.common.collect.ImmutableList;
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
import pl.rafalmag.subtitledownloader.subtitles.SubtitlesList;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
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
    private SubtitlesList subtitlesList;

    @Inject
    private SelectTitleProperties selectTitleProperties;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        StringExpression selectedSubtitlesText = Bindings.concat(
                resources.getString("SelectedSubtitle"), " ", SelectSubtitlesProperties.getInstance()
                        .selectedSubtitlesProperty());
        selectedSubtitles.textProperty().bind(selectedSubtitlesText);

        setTable();

        addUpdateTableListener();
    }

    private void addUpdateTableListener() {
        ObjectProperty<Movie> selectedMovieProperty = selectTitleProperties.selectedMovieProperty();

        ObjectProperty<Movie> lastUpdatedForMovieProperty = subtitlesList.lastUpdatedForMovieProperty();

        BooleanBinding selectedMovieChangedBinding =
                Bindings.notEqual(selectedMovieProperty, lastUpdatedForMovieProperty);
        ReadOnlyBooleanProperty tabSelectedProperty = selectMovieSubtitlesTab.selectedProperty();

        final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty.and(selectedMovieChangedBinding);

        InvalidationListener shouldUpdateSubtitlesListListener = observable -> {
            LOG.trace("observable: " + observable);
            if (shouldUpdateTitlesListBinding.get()) {
                try {
                    subtitlesList.updateList(fxmlMainController.progressBar, 10000);
                    // clear table
                    subtitlesList.listProperty().clear();
                    SelectSubtitlesProperties.getInstance().setSelectedSubtitles(Subtitles.DUMMY_SUBTITLES);
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
        table.setItems(subtitlesList.listProperty());
        table.setPlaceholder(new Label(resources.getString("NoContentInTable")));

        TableColumn<Subtitles, String> fileName = new TableColumn<>(resources.getString("FileName"));
        fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileName.setPrefWidth(500);
        TableColumn<Subtitles, Integer> downloadsCount = new TableColumn<>(resources.getString("Downloads"));
        downloadsCount.setCellValueFactory(new PropertyValueFactory<>("downloadsCount"));

        table.getColumns().setAll(ImmutableList.of(fileName, downloadsCount));
        setSelectionStuff();
    }

    private void setSelectionStuff() {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Subtitles selectedSubtitles = SelectSubtitlesProperties.getInstance().getSelectedSubtitles();
        if (selectedSubtitles != Subtitles.DUMMY_SUBTITLES) {
            table.getSelectionModel().select(selectedSubtitles);
        }

        table.getSelectionModel().getSelectedItems()
                .addListener((InvalidationListener) observable -> {
                    if (table.getSelectionModel().getSelectedItems().size() == 1) {
                        Subtitles subtitles = table.getSelectionModel().getSelectedItems().get(0);
                        Subtitles oldSubtitles = SelectSubtitlesProperties.getInstance().getSelectedSubtitles();
                        LOG.debug("Selected subtitles: {} old: {}", subtitles, oldSubtitles);
                        SelectSubtitlesProperties.getInstance().setSelectedSubtitles(subtitles);
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
