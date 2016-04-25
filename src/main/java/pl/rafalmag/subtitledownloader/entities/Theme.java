package pl.rafalmag.subtitledownloader.entities;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum Theme {
    DEFAULT("Default", ""),
    MODENA("Modena", Application.STYLESHEET_MODENA),
    CASPIAN("Caspian", Application.STYLESHEET_CASPIAN);

    private static final Logger LOG = LoggerFactory.getLogger(InterfaceLanguage.class);

    private static class Holder {
        static Map<String, Theme> MAP = new HashMap<>();
    }

    public static Theme fromNameKey(String nameKey) {
        Theme t = Theme.Holder.MAP.get(nameKey);
        if (t == null) {
            LOG.warn(String.format("Unsupported theme '%s'", nameKey));
            return DEFAULT;
        } else {
            return t;
        }
    }

    private final String nameKey;
    private final String styleSheetUrl;


    Theme(String nameKey, String styleSheetUrl) {
        this.nameKey = nameKey;
        this.styleSheetUrl = styleSheetUrl;
        Holder.MAP.put(nameKey, this);
    }

    public String getStyleSheetUrl() {
        return styleSheetUrl;
    }

    public String getNameKey() {
        return nameKey;
    }

}
