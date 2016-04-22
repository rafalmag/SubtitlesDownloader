package pl.rafalmag.subtitledownloader;

import com.cathive.fx.guice.GuiceApplication;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.gui.FXMLMainController;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;
import pl.rafalmag.subtitledownloader.utils.UTF8Control;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RunMeMain extends GuiceApplication {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RunMeMain.class);

    @Inject
    private Injector injector;

    @Inject
    private GuiceFXMLLoader fxmlLoader;

    private Stage primaryStage;

    public static void main(String[] args) {
        LOGGER.debug("SubtitlesDownloader app started");
        launch(args);
    }

    private static RunMeMain INSTANCE;

    public RunMeMain() {
        INSTANCE = this;
    }

    @Override
    public void init(List<Module> list) throws Exception {
        list.add(new GuiceModule(new Provider<Stage>() {
            @Override
            public Stage get() {
                return primaryStage;
            }
        }));
    }

    public static RunMeMain getInstance() {
        return INSTANCE;
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
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Subtitles Downloader");

        Parent root = getParent(primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Parent getParent(Stage primaryStage) throws IOException {
        URL resource = getClass().getResource("/Main.fxml");
        InterfaceLanguage interfaceLanguage = SubtitlesDownloaderProperties.getInstance().getInterfaceLanguage();
        Locale locale = interfaceLanguage.getLocale();
        Locale.setDefault(locale);
        GuiceFXMLLoader.Result result = fxmlLoader.load(resource,
                ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
        Parent root = result.getRoot();
        FXMLMainController controller = result.getController();
        controller.selectFile(getFileFromCommandLine(), false);
//        controller.setWindow(primaryStage);
//        injector.bind
        return root;
    }

    public void reloadView() throws IOException {
        // replace the content
        AnchorPane content = (AnchorPane) primaryStage.getScene().getRoot();
        content.getChildren().clear();

        File fileBeforeReload = SelectMovieProperties.getInstance().getFile();
        Parent newParent = getParent(primaryStage);
        SelectMovieProperties.getInstance().setFile(fileBeforeReload);
        // replace the content
        content.getChildren().add(newParent);
    }

}
