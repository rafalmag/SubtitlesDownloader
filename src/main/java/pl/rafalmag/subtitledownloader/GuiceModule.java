package pl.rafalmag.subtitledownloader;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import javafx.stage.Stage;
import pl.rafalmag.subtitledownloader.annotations.Slf4jTypeListener;

public class GuiceModule extends AbstractModule {
    private Stage primaryStage;

    public GuiceModule(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void configure() {
//                bind(ResourceBundle.class).annotatedWith(Names.named("i18n-resources"))
//                        .toInstance(ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
        bindListener(Matchers.any(), new Slf4jTypeListener());
        bind(Stage.class).annotatedWith(Names.named("primaryStage")).toProvider(new Provider<Stage>() {
            @Override
            public Stage get() {
                return primaryStage;
            }
        });
    }
}

