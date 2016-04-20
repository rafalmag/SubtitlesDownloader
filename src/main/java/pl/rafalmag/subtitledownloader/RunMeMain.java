package pl.rafalmag.subtitledownloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.gui.FXMLMainController;
import pl.rafalmag.subtitledownloader.utils.UTF8Control;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RunMeMain extends Application {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RunMeMain.class);

    public static void main(String[] args) {
        LOGGER.debug("SubtitlesDownloader app started");
        launch(args);
    }

    /*
     * To allow drag "file on JAR" functionality please drop files on bat/sh "similar" to this:
     * "java.exe" -jar "subtitlesdownloader.jar" %*
     */
    @Nullable
    private File getFileFromCommandLine() {
        List<String> args = getParameters().getRaw();
        if (args.size() == 1) {
            File fileFromArg = new File(args.get(0));
            if (fileFromArg.exists()) {
                return fileFromArg;
            }
        }
        return null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.trace("App started: start");

        primaryStage.setTitle("Subtitles Downloader");

        Parent root = getParent(primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Parent getParent(Stage primaryStage) throws IOException {
        URL resource = getClass().getResource("/Main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Locale locale = SubtitlesDownloaderProperties.getInstance().getUiLocale();
        fxmlLoader.setResources(ResourceBundle.getBundle("opensubtitles", locale, new UTF8Control()));

        Parent root;
        try (InputStream is = resource.openStream()) {
            root = fxmlLoader.load(is);
        }
        FXMLMainController controller = fxmlLoader.getController();
        controller.selectFile(getFileFromCommandLine(), false);
        controller.setWindow(primaryStage);
        return root;
    }

    private void reloadView(Stage stage) throws IOException {
        Parent parent = getParent(stage);
        // replace the content
        AnchorPane content = (AnchorPane) stage.getScene().getRoot();
        content.getChildren().clear();
        content.getChildren().add(parent);
    }

}
