package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.SortedSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;

public class MovieTitlesList {
	private final static ObservableList<Movie> list = FXCollections
			.observableArrayList();

	public static ObservableList<Movie> getList() {
		return list;
	}

	public static void updateList(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		File file = SelectMovieProperties.getInstance().getFile();
		TitleUtils titleUtils = new TitleUtils(file);
		SortedSet<Movie> titles = titleUtils.getTitles(timeoutMs);
		list.setAll(titles);
	}

}
