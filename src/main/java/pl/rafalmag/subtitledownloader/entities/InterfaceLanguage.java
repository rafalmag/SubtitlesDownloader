package pl.rafalmag.subtitledownloader.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum InterfaceLanguage {
    ENGLISH("english", "en-US"),
    POLISH("polski", "pl");

    private static final Logger LOG = LoggerFactory.getLogger(InterfaceLanguage.class);

    private static class Holder {
        static Map<String, InterfaceLanguage> MAP = new HashMap<>();
    }

    public static InterfaceLanguage fromLocale(Locale locale) {
        return fromLanguageTag(locale.toLanguageTag());
    }

    public static InterfaceLanguage fromLanguageTag(String val) {
        InterfaceLanguage t = Holder.MAP.get(val);
        if (t == null) {
            LOG.warn(String.format("Unsupported languageTag '%s'", val));
            return ENGLISH;
        } else {
            return t;
        }
    }

    private final String languageName;
    private final String languageTag;

    InterfaceLanguage(String languageName, String languageTag) {
        this.languageName = languageName;
        this.languageTag = languageTag;
        Holder.MAP.put(languageTag, this);
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(languageTag);
    }

    @Override
    public String toString() {
        return getLanguageName();
    }
}
