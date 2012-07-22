package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.SortedSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

public class SubtitlesList {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SubtitlesList.class);

	private static final ObservableList<Subtitles> list = FXCollections
			.observableArrayList();

	private static final ObjectProperty<Movie> lastUpdatedForMovie = new SimpleObjectProperty<Movie>(
			Movie.DUMMY_MOVIE);

	public static ObservableList<Subtitles> listProperty() {
		return list;
	}

	public static ObjectProperty<Movie> lastUpdatedForMovieProperty() {
		return lastUpdatedForMovie;
	}

	public static void updateList(int timeoutMs) throws InterruptedException {
		LOGGER.debug("updateList timeoutMs=" + timeoutMs);
		Movie selectedMovie = SelectTitleProperties.getInstance()
				.getSelectedMovie();
		File selectedFile = SelectMovieProperties.getInstance().getFile();
		SubtitlesUtils subtitlesUtils = new SubtitlesUtils(selectedMovie,
				selectedFile);
		SortedSet<Subtitles> subtitles = subtitlesUtils.getSubtitles(timeoutMs);
		list.setAll(subtitles);
		lastUpdatedForMovie.setValue(selectedMovie);
	}

}
