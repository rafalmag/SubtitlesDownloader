package pl.rafalmag.subtitledownloader.gui;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.RunMeMain;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.entities.Theme;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    @Inject
    private RunMeMain runMeMain;

    @FXML
    protected TabPane tabPane;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected Button previousButton;

    @FXML
    protected Button nextButton;

    @FXML
    public Menu themeMenu;

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
        initThemeMenu();
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

    private void initThemeMenu() {
        ToggleGroup toggleGroup = new ToggleGroup();

        LinkedHashMap<Theme, RadioMenuItem> map = Arrays.stream(Theme.values()).collect(
                LinkedHashMap::new,
                (m, theme) -> m.put(theme, createThemeMenuItem(toggleGroup, theme)),
                (m, u) -> {
                });
        themeMenu.getItems().addAll(map.values());

        RadioMenuItem radioMenuItem = map.get(subtitlesDownloaderProperties.getTheme());
        toggleGroup.selectToggle(radioMenuItem);
    }

    private RadioMenuItem createThemeMenuItem(ToggleGroup toggleGroup, Theme theme) {
        RadioMenuItem radioMenuItem = new RadioMenuItem(resources.getString(theme.getNameKey()));
        radioMenuItem.setToggleGroup(toggleGroup);
        radioMenuItem.setOnAction(event -> {
            runMeMain.setTheme(theme);
            subtitlesDownloaderProperties.setTheme(theme);
        });
        radioMenuItem.setId(theme.getNameKey());
        return radioMenuItem;
    }

    @FXML
    protected void openAbout() throws IOException {
        LOG.trace("openAbout");
        openModalWindow("AboutSubtitlesDownloader", "/About.fxml");
    }

    private void openModalWindow(String title, String fxmlUrl) throws IOException {
        Stage aboutStage = new Stage(StageStyle.UTILITY);
        aboutStage.initOwner(stage);
        aboutStage.initModality(Modality.WINDOW_MODAL);

        aboutStage.setTitle(resources.getString(title));

        URL resource = getClass().getResource(fxmlUrl);
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
    public void nextTab() {
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
        openModalWindow("Language", "/Language.fxml");
        actionEvent.consume();
    }

    public void openLoginAndPassword(ActionEvent actionEvent) throws IOException {
        LOG.trace("openLoginAndPassword");
        openModalWindow("OsLoginAndPassword", "/OsLogin.fxml");
        actionEvent.consume();
    }
}
