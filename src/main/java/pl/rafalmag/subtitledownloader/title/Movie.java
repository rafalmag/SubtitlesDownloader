package pl.rafalmag.subtitledownloader.title;

import com.google.common.base.Strings;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movie implements Comparable<Movie> {

    public static final Movie DUMMY_MOVIE = new Movie("", 0, 0) {
        @Override
        public String toString() {
            return "";
        }
    };

    private final StringProperty title = new SimpleStringProperty();

    private final IntegerProperty year = new SimpleIntegerProperty();

    private final IntegerProperty imdbId = new SimpleIntegerProperty();

    public Movie(MovieEntity movieEntity) {
        this(movieEntity.getTitle(), movieEntity.getYear(), movieEntity
                .getImdbId());
    }

    public Movie(MovieInfo input) {
        this(input.getTitle(), getYear(input), TitleUtils.getImdbFromString(input.getImdbID()));
    }

    public Movie(String title, int year, int imdbId) {
        setTitle(title);
        setYear(year);
        setImdbId(imdbId);
    }

    // eq. 1977-05-25
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

    private static final Logger LOGGER = LoggerFactory.getLogger(Movie.class);

    protected static int getYear(MovieInfo input) {
        String releaseDate = input.getReleaseDate();
        if (Strings.isNullOrEmpty(releaseDate)) {
            LOGGER.debug("releaseDate is null");
            return -1;
        }
        Matcher matcher = YEAR_PATTERN.matcher(releaseDate);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            LOGGER.warn("releaseDate '" + releaseDate
                    + "' does not contain year");
            return -1;
        }
    }

    public StringProperty titleProperty() {
        return title;
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public IntegerProperty imdbIdProperty() {
        return year;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public void setImdbId(int imdbId) {
        this.imdbId.set(imdbId);
    }

    public String getTitle() {
        return title.get();
    }

    public int getYear() {
        return year.get();
    }

    public int getImdbId() {
        return imdbId.get();
    }

    @Override
    public String toString() {
        return "Movie title=" + getTitle() + ", year=" + getYear() + ", imdbId=" + getImdbId();
    }

    // WARN : hashcode done on the getValues from Properties
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imdbId == null) ? 0 : imdbId.getValue().hashCode());
        result = prime * result + ((title == null) ? 0 : title.getValueSafe().hashCode());
        result = prime * result + ((year == null) ? 0 : year.getValue().hashCode());
        return result;
    }

    // WARN : hashcode done on the getValues from Properties
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie other = (Movie) obj;
        if (imdbId == null) {
            if (other.imdbId != null)
                return false;
        } else if (!imdbId.getValue().equals(other.imdbId.getValue()))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.getValueSafe().equals(other.title.getValueSafe()))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.getValue().equals(other.year.getValue()))
            return false;
        return true;
    }

    @Override
    public int compareTo(Movie o) {
        return getTitle().compareTo(o.getTitle());
    }

}
