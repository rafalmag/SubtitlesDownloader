package pl.rafalmag.subtitledownloader;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.entities.Theme;
import pl.rafalmag.subtitledownloader.opensubtitles.SubtitleLanguageSerializer;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.LoginAndPassword;
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
    private static final String THEME = "theme";
    private static final String OS_LOGIN = "osLogin";
    private static final String OS_PASSWORD_MD5 = "osPasswordMd5";

    public static final SubtitleLanguage DEFAULT_SUBTITLES_LANGUAGE = new SubtitleLanguage("eng", "English", "en");
    public static final SubtitleLanguage EXTRA_SUBTITLES_LANGUAGE = new SubtitleLanguage("pol", "Polish", "pl");
    public static final String ANONYMOUS_LOGIN = "";
    public static final String ANONYMOUS_PASSWORD = "";

    public static final LoginAndPassword ANONYMOUS = new LoginAndPassword(ANONYMOUS_LOGIN, ANONYMOUS_PASSWORD);

    private final Properties properties = new Properties();

    @Inject
    private SubtitleLanguageSerializer subtitleLanguageSerializer;

    public SubtitlesDownloaderProperties() {
        File propertiesFile = new File(PROPERTIES_FILE_NAME);
        if (propertiesFile.isFile()) {
            try (FileInputStream stream = new FileInputStream(propertiesFile)) {
                properties.load(stream);
            } catch (IOException e) {
                LOG.debug("Could not load properties, because of " + e.getMessage(), e);
            }
        }
    }

    private void store() {
        try (FileOutputStream stream = new FileOutputStream(PROPERTIES_FILE_NAME)) {
            properties.store(stream, "Subtitle Downloader properties");
        } catch (IOException e) {
            LOG.error("Could not store properties, because of " + e.getMessage(), e);
        }
    }

    @Nullable
    public File getInitialDir() {
        return Optional.ofNullable(properties.getProperty(INITIAL_DIR)).map(File::new).orElse(null);
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
        return Optional.ofNullable(properties.getProperty(UI_LANGUAGE_TAG))
                .map(InterfaceLanguage::fromLanguageTag)
                .orElse(InterfaceLanguage.ENGLISH);
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

    public Theme getTheme() {
        return Optional.ofNullable(properties.getProperty(THEME))
                .map(Theme::fromNameKey)
                .orElse(Theme.DEFAULT);
    }

    public void setTheme(Theme theme) {
        properties.setProperty(THEME, theme.getNameKey());
        store();
    }

    public LoginAndPassword getLoginAndPassword() {
        Optional<String> loginProperty = Optional.ofNullable(properties.getProperty(OS_LOGIN));
        Optional<String> passwordMd5Property = Optional.ofNullable(properties.getProperty(OS_PASSWORD_MD5));
        if (loginProperty.isPresent() && passwordMd5Property.isPresent()) {
            return new LoginAndPassword(loginProperty.get(), passwordMd5Property.get());
        } else {
            return ANONYMOUS;
        }
    }

    public void setLoginAndPassword(LoginAndPassword loginAndPassword) {
        String login;
        String password;
        if (Strings.isNullOrEmpty(loginAndPassword.getLogin()) || Strings.isNullOrEmpty(loginAndPassword.getPasswordMd5())) {
            login = ANONYMOUS_LOGIN;
            password = ANONYMOUS_PASSWORD;
        } else {
            login = loginAndPassword.getLogin();
            password = loginAndPassword.getPasswordMd5();
        }
        properties.setProperty(OS_LOGIN, login);
        properties.setProperty(OS_PASSWORD_MD5, password);
        store();
    }
}
