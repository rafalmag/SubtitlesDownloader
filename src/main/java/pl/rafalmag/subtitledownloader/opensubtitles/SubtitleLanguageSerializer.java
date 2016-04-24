package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.gson.Gson;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;

public class SubtitleLanguageSerializer {

    private final Gson gson = new Gson();

    public String toString(SubtitleLanguage subtitleLanguage) {
        return gson.toJson(subtitleLanguage);
    }

    public SubtitleLanguage fromString(String serializedSubtitleLanguage) {
        return gson.fromJson(serializedSubtitleLanguage, SubtitleLanguage.class);
    }
}
