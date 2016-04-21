package pl.rafalmag.subtitledownloader.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainSelectedMovieFileTabController extends FXMLMainTab {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FXMLMainSelectedMovieFileTabController.class);

    @FXML
    protected Tab selectMovieFileTab;

    @FXML
    protected Label selectedFile;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        StringExpression selectedMovieText = Bindings.concat(
                resources.getString("SelectedMovie"), " ", SelectMovieProperties.getInstance().movieFileProperty());
        selectedFile.textProperty().bind(selectedMovieText);
    }

    @FXML
    protected void browseFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File initialDir = SelectMovieProperties.getInstance().getInitialDir();
        LOGGER.trace("browseFile initialDir {}", initialDir);
        fileChooser.setInitialDirectory(initialDir);
        fileChooser.setTitle(resources.getString("ChooseMovieFile"));
        File file = fileChooser.showOpenDialog(fxmlMainController.getWindow());
        fxmlMainController.selectFile(file, true);
        event.consume();
    }

}
