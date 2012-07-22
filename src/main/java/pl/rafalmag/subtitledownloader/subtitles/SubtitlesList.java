package pl.rafalmag.subtitledownloader.subtitles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SubtitlesList {

	private final static ObservableList<Subtitles> list = FXCollections
			.observableArrayList();

	public static ObservableList<Subtitles> listProperty() {
		return list;
	}

}
