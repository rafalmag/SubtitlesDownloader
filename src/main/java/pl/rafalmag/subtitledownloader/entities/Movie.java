package pl.rafalmag.subtitledownloader.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie {

	private final StringProperty title = new SimpleStringProperty();

	private final IntegerProperty year = new SimpleIntegerProperty();

	public StringProperty titleProperty() {
		return title;
	}

	public IntegerProperty yearProperty() {
		return year;
	}

}
