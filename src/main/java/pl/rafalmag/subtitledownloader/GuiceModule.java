package pl.rafalmag.subtitledownloader;

import akka.actor.ActorSystem;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import javafx.application.HostServices;
import javafx.stage.Stage;
import pl.rafalmag.subtitledownloader.annotations.I18nResources;
import pl.rafalmag.subtitledownloader.annotations.Slf4jTypeListener;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.utils.UTF8Control;

import java.util.ResourceBundle;

public class GuiceModule extends AbstractModule {
    private Provider<Stage> primaryStage;
    private Provider<HostServices> hostServicesProvider;

    public GuiceModule(Provider<Stage> primaryStage, Provider<HostServices> hostServicesProvider) {
        this.primaryStage = primaryStage;
        this.hostServicesProvider = hostServicesProvider;
    }

    @Override
    public void configure() {
        bindListener(Matchers.any(), new Slf4jTypeListener());
        bind(Stage.class).annotatedWith(Names.named("primaryStage")).toProvider(primaryStage);
        bind(HostServices.class).toProvider(hostServicesProvider);
        bind(CheckMovie.class).to(CheckMovieSubtitles.class);
        bind(ResourceBundle.class).annotatedWith(I18nResources.class)
                .toProvider(() -> ResourceBundle.getBundle("opensubtitles", new UTF8Control()));
        bind(ActorSystem.class).toInstance(ActorSystem.create("TitleService"));
    }
}

