package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;

import java.util.List;

public class SubtitleLanguage {
    private final String id;
    private final String languageName;
    private final String isoCode;

    public SubtitleLanguage(String id, String languageName, String isoCode) {
        this.id = id;
        this.languageName = languageName;
        this.isoCode = isoCode;
    }

    public String getId() {
        return id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubtitleLanguage that = (SubtitleLanguage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (languageName != null ? !languageName.equals(that.languageName) : that.languageName != null) return false;
        return isoCode != null ? isoCode.equals(that.isoCode) : that.isoCode == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (languageName != null ? languageName.hashCode() : 0);
        result = 31 * result + (isoCode != null ? isoCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getLanguageName();
    }

    public static List<SubtitleLanguage> getAllLanguages() {
        try {
            return new Session().getSubLanguages();
        } catch (SubtitlesDownloaderException e) {
            throw new IllegalStateException("Could not get all subtitles languages, because of " + e.getMessage(), e);
        }
    }
}
