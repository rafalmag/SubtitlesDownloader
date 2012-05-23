package pl.rafalmag.subtitledownloader.title;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie {

	private final StringProperty title = new SimpleStringProperty();

	private final IntegerProperty year = new SimpleIntegerProperty();

	private final StringProperty imdbId = new SimpleStringProperty();

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

	public void setImdbId(String imdbId) {
		this.imdbId.set(imdbId);
	}

	public String getTitle() {
		return title.get();
	}

	public int getYear() {
		return year.get();
	}

	public String getImdbId() {
		return imdbId.get();
	}

	@Override
	public String toString() {
		return "Movie [getTitle()=" + getTitle() + ", getYear()=" + getYear()
				+ ", getImdbId()=" + getImdbId() + "]";
	}

}
