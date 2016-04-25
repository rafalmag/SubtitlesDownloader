package pl.rafalmag.subtitledownloader.gui;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.common.base.Throwables;
import com.google.inject.name.Named;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Singleton
public class FXMLMainController implements Initializable {
    @InjectLogger
    private Logger LOG;

    @Inject
    private GuiceFXMLLoader fxmlLoader;

    @Named("primaryStage")
    @Inject
    private Stage stage;

    @Inject
    private SelectMovieProperties selectMovieProperties;

    @FXML
    protected TabPane tabPane;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected Button previousButton;

    @FXML
    protected Button nextButton;
    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        initTab("/MainSelectMovieFileTab.fxml");
        initTab("/MainSelectMovieTitleTab.fxml");
        initTab("/MainSelectMovieSubtitlesTab.fxml");
        initTab("/MainDownloadAndTestTab.fxml");
        progressBar.disableProperty().set(true);
        tabPane.getSelectionModel().select(0);
        previousButton.disableProperty().bind(tabPane.getTabs().get(0).selectedProperty());
        nextButton.disableProperty().bind(tabPane.getTabs().get(tabPane.getTabs().size() - 1).selectedProperty());
    }

    private void initTab(String resourceStr) {
        URL resource = getClass().getResource(resourceStr);
        try {
            TabPane tempTabPane = fxmlLoader.load(resource, resources).getRoot();
            Tab tab = tempTabPane.getTabs().get(0);
            tempTabPane.getTabs().remove(tab);
            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @FXML
    protected void openAbout() throws IOException {
        LOG.trace("openAbout");

        final Stage aboutStage = new Stage(StageStyle.UTILITY);
        aboutStage.initOwner(stage);
        aboutStage.initModality(Modality.WINDOW_MODAL);

        aboutStage.setTitle(resources.getString("AboutSubtitlesDownloader"));

        URL resource = getClass().getResource("/About.fxml");
        Parent aboutView = fxmlLoader.load(resource, resources).getRoot();
        aboutStage.setScene(new Scene(aboutView));
        aboutStage.show();
    }

    @FXML
    protected void closeApp() {
        LOG.trace("closeApp");
        stage.hide();
        LOG.trace("closeApp: hidden");
    }

    @FXML
    protected void nextTab() {
        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
        tabPane.getSelectionModel().select(selectedIndex + 1);
    }

    @FXML
    protected void previousTab() {
        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
        tabPane.getSelectionModel().select(selectedIndex - 1);
    }

    @FXML
    protected void setOnDragOver(DragEvent event) {
        // LOG.trace("setOnDragOver {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            event.acceptTransferModes(TransferMode.LINK);
        }
        event.consume();
    }

    @FXML
    protected void setOnDragEntered(DragEvent event) {
        LOG.trace("setOnDragEntered {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            event.acceptTransferModes(TransferMode.LINK);
        }
        event.consume();
    }

    @FXML
    protected void setOnDragExited(DragEvent event) {
        LOG.trace("setOnDragExited {}", event);
        event.consume();
    }

    @FXML
    protected void setOnDragDropped(DragEvent event) {
        LOG.trace("setOnDragDropped {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            File droppedFile = dragboard.getFiles().get(0);
            LOG.debug("setOnDragDropped file: {}", droppedFile);
            selectFile(droppedFile, false);
        }
        event.consume();
    }

    private boolean isOneFileDragged(Dragboard dragboard) {
        return dragboard.hasFiles() && dragboard.getFiles().size() == 1;
    }

    public void selectFile(File file, boolean setInitialDir) {
        if (file != null) {
            selectMovieProperties.setFile(file);
            if (setInitialDir) {
                selectMovieProperties.setInitialDir(file.getParentFile());
            }
            openTitleTab();
        }
    }

    private void openTitleTab() {
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    public void openLanguage(ActionEvent actionEvent) throws IOException {
        LOG.trace("openLanguage");

        final Stage languageStage = new Stage(StageStyle.UTILITY);
        languageStage.initOwner(stage);
        languageStage.initModality(Modality.WINDOW_MODAL);

        languageStage.setTitle(resources.getString("Language"));

        URL resource = getClass().getResource("/Language.fxml");
        Parent aboutView = fxmlLoader.load(resource, resources).getRoot();
        languageStage.setScene(new Scene(aboutView));
        languageStage.show();
        languageStage.sizeToScene();

        actionEvent.consume();
    }
}
