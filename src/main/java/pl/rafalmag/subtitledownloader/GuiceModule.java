package pl.rafalmag.subtitledownloader;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import javafx.stage.Stage;
import pl.rafalmag.subtitledownloader.annotations.I18nResources;
import pl.rafalmag.subtitledownloader.annotations.Slf4jTypeListener;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.utils.UTF8Control;

import java.util.ResourceBundle;

public class GuiceModule extends AbstractModule {
    private Provider<Stage> primaryStage;

    public GuiceModule(Provider<Stage> primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void configure() {
        bindListener(Matchers.any(), new Slf4jTypeListener());
        bind(Stage.class).annotatedWith(Names.named("primaryStage")).toProvider(primaryStage);
        bind(CheckMovie.class).to(CheckMovieSubtitles.class);
        bind(ResourceBundle.class).annotatedWith(I18nResources.class)
                .toProvider(() -> ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
    }
}

