package pl.rafalmag.subtitledownloader.opensubtitles.entities;

public class SubtitleLanguage {
    private String id;
    private String languageName;
    private String isoCode;

    public SubtitleLanguage() {

    }

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

    public void setId(String id) {
        this.id = id;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
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
}
