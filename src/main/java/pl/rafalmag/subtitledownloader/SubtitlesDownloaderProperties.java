package pl.rafalmag.subtitledownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class SubtitlesDownloaderProperties {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SubtitlesDownloaderProperties.class);

    private static final String PROPERTIES_FILE_NAME = "subtitleDownloader.properties";

    private static final String INITIAL_DIR = "initialDir";
    private static final String UI_LANGUAGE = "uiLanguage";
    private static final String SUBTITLES_LANGUAGE = "subtitlesLanguage";

    private static class SubtitlesDownloaderPropertiesHolder {
        private static SubtitlesDownloaderProperties instance = new SubtitlesDownloaderProperties();
    }

    public static SubtitlesDownloaderProperties getInstance() {
        return SubtitlesDownloaderPropertiesHolder.instance;
    }

    private final Properties properties = new Properties();

    private SubtitlesDownloaderProperties() {
        load();
    }

    private void load() {
        try {
            properties.load(new FileInputStream(PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            LOGGER.debug("Could not load properties, because of " + e.getMessage(), e);
        }
    }

    private void store() {
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE_NAME),
                    "Subtitle Downloader properties");
        } catch (IOException e) {
            LOGGER.error("Could not store properties, because of " + e.getMessage(), e);
        }
    }

    @Nullable
    public File getInitialDir() {
        String initialDir = properties.getProperty(INITIAL_DIR);
        if (initialDir == null) {
            return null;
        } else {
            return new File(initialDir);
        }
    }

    public Locale getUiLocale() {
        return new Locale(properties.getProperty(UI_LANGUAGE, "en_US"));
    }

    public String getSubtitlesLanguage() {
        return properties.getProperty(SUBTITLES_LANGUAGE, "en");
    }

    public void setInitialDir(@Nullable File initialDir) {
        if (initialDir == null) {
            properties.setProperty(INITIAL_DIR, null);
        } else {
            properties.setProperty(INITIAL_DIR, initialDir.getPath());
        }
        store();
    }
}
