package pl.rafalmag.subtitledownloader.title;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;

import com.moviejukebox.themoviedb.model.MovieDb;

public class Movie {

	// TODO movie file ?

	private final StringProperty title = new SimpleStringProperty();

	private final IntegerProperty year = new SimpleIntegerProperty();

	private final IntegerProperty imdbId = new SimpleIntegerProperty();

	public Movie(MovieEntity movieEntity) {
		this(movieEntity.getTitle(), movieEntity.getYear(), movieEntity
				.getImdbId());
	}

	public Movie(MovieDb input) {
		this(input.getTitle(), getYear(input), TitleUtils
				.getImdbFromString(input.getImdbID()));
	}

	public Movie(String title, int year, int imdbId) {
		setTitle(title);
		setYear(year);
		setImdbId(imdbId);
	}

	// eq. 1977-05-25
	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

	protected static int getYear(MovieDb input) {
		String releaseDate = input.getReleaseDate();
		Matcher matcher = YEAR_PATTERN.matcher(releaseDate);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		return 0;
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
		return "Movie [title=" + getTitle() + ", year=" + getYear()
				+ ", imdbId=" + getImdbId() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imdbId == null) ? 0 : imdbId.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		Movie other = (Movie) obj;
		if (imdbId == null) {
			if (other.imdbId != null)
				return false;
		} else if (!imdbId.equals(other.imdbId))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

}
