package pl.rafalmag.subtitledownloader.gui;

import javafx.fxml.FXML;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FXMLAboutController {

    @InjectLogger
    private Logger LOG;

    protected static final String url = "http://www.opensubtitles.org/";

    @FXML
    protected void openUrl() {
        LOG.debug("openUrl");
        try {
            final URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            LOG.error(
                    "Could not open URL " + url + " because of "
                            + e.getMessage(), e);
        }
    }

}
