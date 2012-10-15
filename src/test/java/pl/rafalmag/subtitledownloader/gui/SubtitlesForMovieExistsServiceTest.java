package pl.rafalmag.subtitledownloader.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.rafalmag.subtitledownloader.gui.properties.SynchronizedReadOnlyProperty;
import pl.rafalmag.subtitledownloader.gui.properties.SynchronizedWritableValue;
import pl.rafalmag.subtitledownloader.title.Movie;

public class SubtitlesForMovieExistsServiceTest {

	private File movieFile;
	private File subtitlesFile;
	private File tempDirectory;
	private Movie movie;
	private ReadOnlyProperty<Movie> movieProperty;
	private final WritableValue<Boolean> movieHasSubtitles = new SynchronizedWritableValue<>(
			new SimpleBooleanProperty());

	// public static class MyApplication extends Application {
	//
	// @Override
	// public void start(Stage primaryStage) throws Exception {
	// }
	// }
	//
	// @BeforeClass
	// public static void initJavaFx() {
	// new Thread("JavaFX") {
	// @Override
	// public void run() {
	// Application.launch(MyApplication.class);
	// }
	// }.start();
	// }

	@Before
	public void createTempFiles() throws Exception {
		tempDirectory = Files.createTempDirectory("subtitlesDownloader")
				.toFile();
		movieFile = new File(tempDirectory, "movie.avi");
		movie = new Movie("title", 2000, 1, movieFile);
		movieProperty = new SynchronizedReadOnlyProperty<>(
				new SimpleObjectProperty<>(movie));
		movieFile.createNewFile();
		subtitlesFile = new File(tempDirectory, "movie.srt");
		subtitlesFile.createNewFile();
	}

	@After
	public void cleanUp() throws Exception {
		movieFile.delete();
		subtitlesFile.delete();
		tempDirectory.delete();
	}

	@Test
	public void should_set_false_if_no_movie() throws Exception {
		// given
		// when
		SubtitlesForMovieExistsService service = new SubtitlesForMovieExistsService(
				movieProperty, movieHasSubtitles);
		movieFile.delete();
		service.register();
		Thread.sleep(3000);

		// then
		assertFalse(movieHasSubtitles.getValue());
	}

	@Test
	public void should_set_false_if_no_subtitles() throws Exception {
		// given

		// when
		SubtitlesForMovieExistsService service = new SubtitlesForMovieExistsService(
				movieProperty, movieHasSubtitles);
		subtitlesFile.delete();
		service.register();
		Thread.sleep(1000);

		// then
		assertFalse(movieHasSubtitles.getValue());
	}

	@Test
	public void should_set_true_if_movie_and_subtitles() throws Exception {
		// given

		// when
		SubtitlesForMovieExistsService service = new SubtitlesForMovieExistsService(
				movieProperty, movieHasSubtitles);
		service.register();
		Thread.sleep(1000);

		// then
		assertTrue(movieHasSubtitles.getValue());
	}

	@Test
	public void should_set_true_if_subtitles_already_downloaded()
			throws Exception {
		// given
		// when
		SubtitlesForMovieExistsService service = new SubtitlesForMovieExistsService(
				movieProperty, movieHasSubtitles);
		subtitlesFile.delete();
		service.register();
		Thread.sleep(1000);
		subtitlesFile.createNewFile();
		Thread.sleep(1000);

		// then
		assertTrue(movieHasSubtitles.getValue());
	}

}
