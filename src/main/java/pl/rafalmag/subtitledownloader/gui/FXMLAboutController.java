package pl.rafalmag.subtitledownloader.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.fxml.FXML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLAboutController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FXMLAboutController.class);

	protected static final String url = "http://www.opensubtitles.org/";

	@FXML
	protected void openUrl() {
		LOGGER.debug("openUrl");
		try {
			final URI uri = new URI(url);
			Desktop.getDesktop().browse(uri);
		} catch (IOException | URISyntaxException e) {
			LOGGER.error(
					"Could not open URL " + url + " because of "
							+ e.getMessage(), e);
		}
	}

}
