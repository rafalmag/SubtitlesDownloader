package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.gui.SelectMovieProperties;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;
import pl.rafalmag.subtitledownloader.utils.TaskWithProgressCallback;

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

	private static final ExecutorService EXECUTOR = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.daemon(true)
					.namingPattern("SubtitlesUpdate-%d")
					.build());

	public static void updateList(final ProgressBar progressBar,
			final long timeoutMs) throws InterruptedException {
		LOGGER.debug("SubtitlesList update timeout " + timeoutMs + "ms");
		progressBar.disableProperty().set(false);
		final Movie selectedMovie = SelectTitleProperties.getInstance()
				.getSelectedMovie();
		final File selectedFile = SelectMovieProperties.getInstance().getFile();
		TaskWithProgressCallback<Void> task = new TaskWithProgressCallback<Void>() {

			@Override
			protected Void call() throws Exception {
				SubtitlesUtils subtitlesUtils = new SubtitlesUtils(
						selectedMovie,
						selectedFile, timeoutMs, this);
				final SortedSet<Subtitles> subtitles = subtitlesUtils
						.getSubtitles();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						list.setAll(subtitles);
						lastUpdatedForMovie.setValue(selectedMovie);
						progressBar.disableProperty().set(true);
					}

				});
				return null;
			}
		};
		progressBar.progressProperty().bind(
				task.progressProperty());
		EXECUTOR.submit(task);
	}

}
