package pl.rafalmag.subtitledownloader.gui;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public class FXMLAboutController {

    @InjectLogger
    private Logger LOG;

    @Inject
    private HostServices hostServices;

    protected static final String OS_URL = "http://www.opensubtitles.org/";

    @FXML
    protected void openUrl(ActionEvent actionEvent) {
        LOG.debug("openUrl");
        try {
            final URI uri = new URI(OS_URL);
            hostServices.showDocument(uri.toString());
        } catch (URISyntaxException e) {
            LOG.error("Could not open URL " + OS_URL + " because of " + e.getMessage(), e);
        }
        actionEvent.consume();
    }

}
