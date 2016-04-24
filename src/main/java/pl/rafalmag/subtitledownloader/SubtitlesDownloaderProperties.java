package pl.rafalmag.subtitledownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.opensubtitles.SubtitleLanguageSerializer;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

@Singleton
public class SubtitlesDownloaderProperties {

    // cannot be injected as it is used in constructor
    private static final Logger LOG = LoggerFactory.getLogger(SubtitlesDownloaderProperties.class);

    private static final String PROPERTIES_FILE_NAME = "subtitleDownloader.properties";

    private static final String INITIAL_DIR = "initialDir";
    private static final String UI_LANGUAGE_TAG = "uiLanguageTag";
    private static final String SUBTITLES_LANGUAGE = "subtitlesLanguage";

    public static final String DEFAULT_UI_LANGUAGE_TAG = "en-US";
    public static final SubtitleLanguage DEFAULT_SUBTITLES_LANGUAGE = new SubtitleLanguage("eng", "English", "en");
    public static final SubtitleLanguage EXTRA_SUBTITLES_LANGUAGE = new SubtitleLanguage("pol", "Polish", "pl");

    private final Properties properties = new Properties();

    @Inject
    private SubtitleLanguageSerializer subtitleLanguageSerializer;

    public SubtitlesDownloaderProperties() {
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

    public void setInitialDir(@Nullable File initialDir) {
        if (initialDir == null) {
            properties.setProperty(INITIAL_DIR, null);
        } else {
            properties.setProperty(INITIAL_DIR, initialDir.getPath());
        }
        store();
    }

    public InterfaceLanguage getInterfaceLanguage() {
        return InterfaceLanguage.fromLanguageTag(properties.getProperty(UI_LANGUAGE_TAG, DEFAULT_UI_LANGUAGE_TAG));
    }

    public void setInterfaceLanguage(InterfaceLanguage interfaceLanguage) {
        Locale.setDefault(interfaceLanguage.getLocale());
        properties.setProperty(UI_LANGUAGE_TAG, interfaceLanguage.getLanguageTag());
        store();
    }

    public SubtitleLanguage getSubtitlesLanguage() {
        return Optional.ofNullable(properties.getProperty(SUBTITLES_LANGUAGE))
                .map(subtitleLanguageSerializer::fromString)
                .orElse(DEFAULT_SUBTITLES_LANGUAGE);
    }

    public void setSubtitlesLanguage(SubtitleLanguage subtitleLanguage) {
        properties.setProperty(SUBTITLES_LANGUAGE, subtitleLanguageSerializer.toString(subtitleLanguage));
        store();
    }
}
