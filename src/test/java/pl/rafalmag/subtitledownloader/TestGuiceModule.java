package pl.rafalmag.subtitledownloader;

import javafx.stage.Stage;

import static org.mockito.Mockito.mock;

public class TestGuiceModule extends GuiceModule {
    public TestGuiceModule() {
        super(() -> mock(Stage.class));
    }
}
