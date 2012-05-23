package pl.rafalmag.subtitledownloader.entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovieTitlesList {
	private final static ObservableList<Movie> list = FXCollections
			.observableArrayList();

	public static ObservableList<Movie> getList() {
		return list;
	}

}
