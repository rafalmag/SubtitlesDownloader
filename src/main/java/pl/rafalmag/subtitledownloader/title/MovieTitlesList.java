package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.SortedSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;

public class MovieTitlesList {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MovieTitlesList.class);

	private final static StringProperty lastUpdatedForFilePath = new SimpleStringProperty(
			"");

	private final static ObservableList<Movie> list = FXCollections
			.observableArrayList();

	public static ObservableList<Movie> listProperty() {
		return list;
	}

	public static StringProperty lastUpdatedForFilePathProperty() {
		return lastUpdatedForFilePath;
	}

	public static void updateList(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		LOGGER.debug("updateList timeoutMs=" + timeoutMs);
		File file = SelectMovieProperties.getInstance().getFile();
		TitleUtils titleUtils = new TitleUtils(file);
		SortedSet<Movie> titles = titleUtils.getTitles(timeoutMs);
		list.setAll(titles);
		lastUpdatedForFilePath.setValue(SelectMovieProperties.getInstance()
				.getFilePath());
	}

}
