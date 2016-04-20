package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MovieEntity {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MovieEntity.class);

    String movieHash;
    int imdbId;
    String title;
    int year;
    int seenCount;

    public MovieEntity(Map<String, Object> record) {
        movieHash = (String) record.get("MovieHash");
        imdbId = objectToInt(record.get("MovieImdbID"), "imdbId");
        title = (String) record.get("MovieName");
        year = objectToInt(record.get("MovieYear"), "year");
        seenCount = objectToInt(record.get("SeenCount"), "seanCount");
        LOGGER.debug("parsed checkMovieHash2Entity={}", this);
    }

    private static int objectToInt(Object intObject, String objectName) {
        if (intObject instanceof Integer) {
            return (Integer) intObject;
        } else if (intObject instanceof String) {
            String string = (String) intObject;
            if (Strings.isNullOrEmpty(string)) {
                LOGGER.debug(objectName + " is null");
                return -1;
            } else {
                return Integer.parseInt(string);
            }
        } else {
            throw new IllegalStateException("Unsupported object type: "
                    + intObject);
        }
    }

    public MovieEntity(String movieHash, int imdbId, String title, int year,
                       int seenCount) {
        this.movieHash = movieHash;
        this.imdbId = imdbId;
        this.title = title;
        this.year = year;
        this.seenCount = seenCount;
    }

    public String getMovieHash() {
        return movieHash;
    }

    public void setMovieHash(String movieHash) {
        this.movieHash = movieHash;
    }

    public int getImdbId() {
        return imdbId;
    }

    public void setImdbId(int imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeenCount() {
        return seenCount;
    }

    public void setSeenCount(int seenCount) {
        this.seenCount = seenCount;
    }

    @Override
    public String toString() {
        return "CheckMovieHash2Entity [movieHash=" + movieHash + ", imdbId="
                + imdbId + ", title=" + title + ", year=" + year
                + ", seenCount=" + seenCount + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + imdbId;
        result = prime * result
                + ((movieHash == null) ? 0 : movieHash.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + seenCount;
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MovieEntity other = (MovieEntity) obj;
        if (imdbId != other.imdbId)
            return false;
        if (movieHash == null) {
            if (other.movieHash != null)
                return false;
        } else if (!movieHash.equals(other.movieHash))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (seenCount != other.seenCount)
            return false;
        if (year != other.year)
            return false;
        return true;
    }

}
