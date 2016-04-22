package pl.rafalmag.subtitledownloader.gui;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.common.base.Throwables;
import com.google.inject.name.Named;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.utils.UTF8Control;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXMLMainController.class);

    @Inject
    private GuiceFXMLLoader fxmlLoader;

    @Named("primaryStage")
    @Inject
    private Stage stage;

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
            tabPane.getTabs().add(tempTabPane.getTabs().get(0));
            tempTabPane.getTabs().remove(0);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

//    public Window getWindow() {
//        return stage;
//    }
//
//    public void setWindow(Window window) {
//        this.window = window;
//    }

    @FXML
    protected void openAbout() throws IOException {
        LOGGER.trace("openAbout");

        final Stage aboutStage = new Stage(StageStyle.UTILITY);
        aboutStage.initOwner(stage);
        aboutStage.initModality(Modality.WINDOW_MODAL);

        aboutStage.setTitle(resources.getString("AboutSubtitlesDownloader"));

        URL resource = getClass().getResource("/About.fxml");
        Parent aboutView = FXMLLoader.load(resource, ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
        aboutStage.setScene(new Scene(aboutView));
        aboutStage.show();
    }

    @FXML
    protected void closeApp() {
        LOGGER.trace("closeApp");
        stage.hide();
        LOGGER.trace("closeApp: hidden");
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
        // LOGGER.trace("setOnDragOver {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            event.acceptTransferModes(TransferMode.LINK);
        }
        event.consume();
    }

    @FXML
    protected void setOnDragEntered(DragEvent event) {
        LOGGER.trace("setOnDragEntered {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            event.acceptTransferModes(TransferMode.LINK);
        }
        event.consume();
    }

    @FXML
    protected void setOnDragExited(DragEvent event) {
        LOGGER.trace("setOnDragExited {}", event);
        event.consume();
    }

    @FXML
    protected void setOnDragDropped(DragEvent event) {
        LOGGER.trace("setOnDragDropped {}", event);
        Dragboard dragboard = event.getDragboard();
        if (isOneFileDragged(dragboard)) {
            File droppedFile = dragboard.getFiles().get(0);
            LOGGER.debug("setOnDragDropped file: {}", droppedFile);
            selectFile(droppedFile, false);
        }
        event.consume();
    }

    private boolean isOneFileDragged(Dragboard dragboard) {
        return dragboard.hasFiles() && dragboard.getFiles().size() == 1;
    }

    public void selectFile(File file, boolean setInitialDir) {
        if (file != null) {
            SelectMovieProperties.getInstance().setFile(file);
            if (setInitialDir) {
                SelectMovieProperties.getInstance().setInitialDir(file.getParentFile());
            }
            openTitleTab();
        }
    }

    private void openTitleTab() {
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    public void openLanguage(ActionEvent actionEvent) throws IOException {
        LOGGER.trace("openLanguage");

        final Stage languageStage = new Stage(StageStyle.UTILITY);
        languageStage.initOwner(stage);
        languageStage.initModality(Modality.WINDOW_MODAL);

        languageStage.setTitle(resources.getString("Language"));

        URL resource = getClass().getResource("/Language.fxml");
        Parent aboutView = FXMLLoader.load(resource, ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
        languageStage.setScene(new Scene(aboutView));
        languageStage.show();
        languageStage.sizeToScene();

        actionEvent.consume();
    }
}
