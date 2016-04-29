package pl.rafalmag.subtitledownloader.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;

import javax.inject.Singleton;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public class FXMLAboutController {

    @InjectLogger
    private Logger LOG;

    protected static final String OS_URL = "http://www.opensubtitles.org/";

    @FXML
    protected void openUrl(ActionEvent actionEvent) {
        LOG.debug("openUrl");
        try {
            final URI uri = new URI(OS_URL);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            LOG.error("Could not open URL " + OS_URL + " because of " + e.getMessage(), e);
        }
        actionEvent.consume();
    }

}
