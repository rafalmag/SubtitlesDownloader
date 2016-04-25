package pl.rafalmag.subtitledownloader.gui;

import com.google.common.collect.ImmutableList;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.MovieTitlesListService;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.ResourceBundle;

@Singleton
public class FXMLMainSelectedMovieTitleTabController implements Initializable {

    @InjectLogger
    private Logger LOG;

    @FXML
    protected Tab selectMovieTitleTab;

    @FXML
    protected Label selectedTitle;

    @FXML
    protected TableView<Movie> table;

    @Inject
    protected FXMLMainController fxmlMainController;

    @Inject
    private MovieTitlesListService movieTitlesListService;

    @Inject
    private SelectTitleProperties selectTitleProperties;

    @Inject
    private SelectMovieProperties selectMovieProperties;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        StringExpression selectedTitleText = Bindings.concat(
                resources.getString("SelectedTitle"), " ", selectTitleProperties.selectedMovieProperty());
        selectedTitle.textProperty().bind(selectedTitleText);

        setTable();

        addUpdateTableListener();
    }

    private void addUpdateTableListener() {
        StringProperty movieFileProperty = selectMovieProperties.movieFileProperty();

        StringProperty lastUpdatedForFilePathProperty = movieTitlesListService.lastUpdatedForFilePathProperty();

        BooleanBinding movieFilePathChangedBinding = Bindings.notEqual(movieFileProperty, lastUpdatedForFilePathProperty);
        ReadOnlyBooleanProperty tabSelectedProperty = selectMovieTitleTab.selectedProperty();

        final BooleanBinding shouldUpdateTitlesListBinding = tabSelectedProperty.and(movieFilePathChangedBinding);

        InvalidationListener shouldUpdateTitlesListListener = observable -> {
            LOG.trace("observable: " + observable);
            if (shouldUpdateTitlesListBinding.get()) {
                refreshTable();

                // clear table
                movieTitlesListService.listProperty().clear();

                selectTitleProperties.setSelectedMovie(Movie.DUMMY_MOVIE);
            }
        };

        tabSelectedProperty.addListener(shouldUpdateTitlesListListener);
        movieFileProperty.addListener(shouldUpdateTitlesListListener);
        lastUpdatedForFilePathProperty.addListener(shouldUpdateTitlesListListener);
    }

    private void setTable() {
        table.setItems(movieTitlesListService.listProperty());
        table.setPlaceholder(new Label(resources.getString("NoContentInTable")));

        TableColumn<Movie, String> title = new TableColumn<>(resources.getString("Title"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        title.setPrefWidth(500);
        TableColumn<Movie, Integer> year = new TableColumn<>(resources.getString("Year"));
        year.setCellValueFactory(new PropertyValueFactory<>("year"));
        //double click on row navigates to next tab
        table.setRowFactory(tv -> {
            TableRow<Movie> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Movie rowData = row.getItem();
                    LOG.trace("Double clicked {}", rowData);
                    fxmlMainController.nextTab();
                }
            });
            return row;
        });

        table.getColumns().setAll(ImmutableList.of(title, year));
        setSelectionStuff();
    }

    private void setSelectionStuff() {
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // SelectTitleProperties.getInstance().selectedMovieProperty()
        // .bind(table.getSelectionModel().selectedItemProperty());
        Movie selectedMovie = selectTitleProperties.getSelectedMovie();
        if (selectedMovie != Movie.DUMMY_MOVIE) {
            table.getSelectionModel().select(selectedMovie);
        }
        table.getSelectionModel().getSelectedItems()
                .addListener((InvalidationListener) observable -> {
                    if (table.getSelectionModel().getSelectedItems().size() == 1) {
                        Movie movie = table.getSelectionModel().getSelectedItems().get(0);
                        Movie oldSelectedMovie = selectTitleProperties.getSelectedMovie();
                        LOG.debug("Selected movie: {}, old: {}", movie, oldSelectedMovie);

                        selectTitleProperties.setSelectedMovie(movie);
                        if (oldSelectedMovie == movie) {
                            // item was double clicked
                            fxmlMainController.nextTab();
                        }
                    } else {
                        // SelectTitleProperties.getInstance()
                        // .setSelectedMovie(Movie.DUMMY_MOVIE);
                    }
                });
    }

    @FXML
    protected void refreshTable() {
        LOG.trace("refresh");
        try {
            movieTitlesListService.updateList(fxmlMainController.progressBar, 10000);
        } catch (InterruptedException e) {
            LOG.error("Could not update titles list", e);
        }
    }
}
