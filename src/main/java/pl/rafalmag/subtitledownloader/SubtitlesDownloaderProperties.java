package pl.rafalmag.subtitledownloader;

import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class SubtitlesDownloaderProperties {

    @InjectLogger
    private Logger LOG;

    private static final String PROPERTIES_FILE_NAME = "subtitleDownloader.properties";

    private static final String INITIAL_DIR = "initialDir";
    private static final String UI_LANGUAGE_TAG = "uiLanguageTag";
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
            LOG.debug("Could not load properties, because of " + e.getMessage(), e);
        }
    }

    private void store() {
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE_NAME),
                    "Subtitle Downloader properties");
        } catch (IOException e) {
            LOG.error("Could not store properties, because of " + e.getMessage(), e);
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

    public InterfaceLanguage getInterfaceLanguage() {
        return InterfaceLanguage.fromLanguageTag(properties.getProperty(UI_LANGUAGE_TAG, "en-US"));
    }

    public void setInterfaceLanguage(InterfaceLanguage interfaceLanguage) {
        Locale.setDefault(interfaceLanguage.getLocale());
        properties.setProperty(UI_LANGUAGE_TAG, interfaceLanguage.getLanguageTag());
        store();
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
