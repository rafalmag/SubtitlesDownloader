package pl.rafalmag.subtitledownloader;

import akka.actor.ActorSystem;
import com.cathive.fx.guice.GuiceApplication;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Module;
import com.google.inject.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.annotations.I18nResources;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.entities.Theme;
import pl.rafalmag.subtitledownloader.gui.FXMLMainController;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class RunMeMain extends GuiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunMeMain.class);

    @Inject
    private Injector injector;

    @Inject
    private GuiceFXMLLoader fxmlLoader;

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    @Inject
    private SelectMovieProperties selectMovieProperties;

    @I18nResources
    @Inject
    private Provider<ResourceBundle> resources;

    @Inject
    private ActorSystem actorSystem;

    private Stage primaryStage;

    public static void main(String[] args) {
        LOGGER.debug("SubtitlesDownloader app started");
        launch(args);
    }

    @Override
    public void init(List<Module> list) throws Exception {
        list.add(new GuiceModule(() -> primaryStage, this::getHostServices));
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
        primaryStage.getIcons().addAll(new Image("/icon64.png"),
                new Image("/icon32.png"),
                new Image("/icon24.png"),
                new Image("/icon16.png"));

        Parent root = getParent();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        setTheme(subtitlesDownloaderProperties.getTheme());
    }

    public void setTheme(Theme theme) {
        if (theme == Theme.DEFAULT) {
            Application.setUserAgentStylesheet(Theme.DEFAULT.getStyleSheetUrl());
        } else {
            Application.setUserAgentStylesheet(theme.getStyleSheetUrl());
        }
    }

    private Parent getParent() throws IOException {
        InterfaceLanguage interfaceLanguage = subtitlesDownloaderProperties.getInterfaceLanguage();
        Locale locale = interfaceLanguage.getLocale();
        Locale.setDefault(locale);
        GuiceFXMLLoader.Result result = fxmlLoader.load(getClass().getResource("/Main.fxml"), resources.get());
        Parent root = result.getRoot();
        FXMLMainController controller = result.getController();
        controller.selectFile(getFileFromCommandLine(), false);
        return root;
    }

    public void reloadView() throws IOException {
        Scene scene = primaryStage.getScene();

        // clear old content
        Pane content = (Pane) scene.getRoot();
        content.getChildren().clear();

        File fileBeforeReload = selectMovieProperties.getFile();
        Parent newParent = getParent();
        selectMovieProperties.setFile(fileBeforeReload);

        // replace the content
        scene.setRoot(newParent);
    }

    @Override
    public void stop() throws Exception {
        LOGGER.trace("stop");
        super.stop();
        actorSystem.terminate();
        LOGGER.trace("stop: after calling terminate (may be still terminating)");
    }
}
