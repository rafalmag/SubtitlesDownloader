package pl.rafalmag.subtitledownloader.gui;

import com.google.inject.name.Named;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Singleton
public class FXMLMainSelectedMovieFileTabController implements Initializable {
    @InjectLogger
    private Logger LOG;

    @FXML
    protected Tab selectMovieFileTab;

    @FXML
    protected Label selectedFile;

    @Named("primaryStage")
    @Inject
    private Stage stage;

    @Inject
    protected FXMLMainController fxmlMainController;

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
        LOG.trace("browseFile initialDir {}", initialDir);
        fileChooser.setInitialDirectory(initialDir);
        fileChooser.setTitle(resources.getString("ChooseMovieFile"));
        File file = fileChooser.showOpenDialog(stage);
        fxmlMainController.selectFile(file, true);
        event.consume();
    }

}
