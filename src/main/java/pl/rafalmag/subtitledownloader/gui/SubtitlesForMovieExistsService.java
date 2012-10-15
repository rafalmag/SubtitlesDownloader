package pl.rafalmag.subtitledownloader.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.WritableValue;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.gui.properties.SynchronizedReadOnlyProperty;
import pl.rafalmag.subtitledownloader.gui.properties.SynchronizedWritableValue;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.SelectTitleProperties;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public class SubtitlesForMovieExistsService {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(SubtitlesForMovieExistsService.class);

	private final ExecutorService executor = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.daemon(true).namingPattern("File watcher-%d").build());

	private final WatchService fileWatcher;
	// should be accessed only in JavaFx thread
	private WatchKey fileWatcherKey;
	// should be accessed only in JavaFx thread
	private Future<?> dirListenerFuture;

	private final ReadOnlyProperty<Movie> movieProperty;

	private final WritableValue<Boolean> movieHasSubtitles;

	@Deprecated
	public SubtitlesForMovieExistsService(BooleanProperty movieHasSubtitles)
			throws IOException {
		this(SelectTitleProperties.getInstance().selectedMovieProperty(),
				movieHasSubtitles);
	}

	public SubtitlesForMovieExistsService(
			final ReadOnlyProperty<Movie> movieProperty,
			WritableValue<Boolean> movieHasSubtitles) throws IOException {
		this.movieProperty = new SynchronizedReadOnlyProperty<>(movieProperty);
		this.movieHasSubtitles = new SynchronizedWritableValue<>(
				movieHasSubtitles);
		fileWatcher = FileSystems.getDefault().newWatchService();
		propertyListener = new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				LOGGER.trace("property invalidated");
				dirListenerFuture.cancel(true);
				if (fileWatcherKey != null) {
					fileWatcherKey.cancel();
				}
				registerDirListener(movieProperty);
			}

		};
	}

	private void registerDirListener(
			final ReadOnlyProperty<Movie> movieProperty) {
		try {
			File movieFile = movieProperty.getValue().getMovieFile();
			File movieDir = movieFile.getParentFile();
			fileWatcherKey = movieDir.toPath().register(
					fileWatcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE);
			dirListenerFuture = executor.submit(new DirListener(
					movieFile));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void register() {
		// dirListenerFuture = executor.submit(new
		// DirListener(movieProperty.get()
		// .getMovieFile()));
		movieProperty.addListener(propertyListener);
		registerDirListener(movieProperty);

		LOGGER.trace("listener added");
	}

	// public void deregister() {
	// dirListenerFuture.cancel(true);
	// movieProperty.removeListener(propertyListener);
	// }

	private void setMovieHasSubtitles(File movieFile, String movieDir,
			String movieBaseFileName) {
		if (!movieFile.isFile()) {
			return;
		}
		boolean movieHasSubtitles = false;
		for (String extension : EXTENSIONS) {
			if (new File(movieDir, movieBaseFileName + extension)
					.exists()) {
				movieHasSubtitles = true;
				break;
			}
		}
		setMovieHasSubtitles(movieHasSubtitles);
	}

	private void setMovieHasSubtitles(final boolean value) {
		LOGGER.trace("movieHasSubtitles = {}", value);
		// if (Platform.isFxApplicationThread()) {
		movieHasSubtitles.setValue(value);
		// } else {
		// Platform.runLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// movieHasSubtitles.setValue(value);
		// }
		// });
		// }
	}

	private static final List<String> EXTENSIONS = ImmutableList.of(".srt",
			".sub", ".txt");

	private final InvalidationListener propertyListener;

	private class DirListener implements Runnable {

		private final String moviePathDir;
		private final String movieBaseFileName;
		private final File movieFile;

		public DirListener(File movieFile) {
			this.movieFile = movieFile;
			this.moviePathDir = movieFile.getParent();
			this.movieBaseFileName = FilenameUtils.getBaseName(movieFile
					.getName());
		}

		@Override
		public void run() {
			LOGGER.trace("DirListener run");
			try {
				setMovieHasSubtitles(movieFile, moviePathDir, movieBaseFileName);
				while (true) {
					WatchKey key = fileWatcher.take();
					if (LOGGER.isTraceEnabled()) {
						Collection<String> events = Collections2.transform(
								key.pollEvents(),
								new Function<WatchEvent<?>, String>() {

									@Override
									public String apply(
											@Nullable WatchEvent<?> input) {
										return "context =" + input.context()
												+ " kind="
												+ input.kind().name();
									}

								});
						LOGGER.trace(
								"DirListener after take key valid ={} events={}",
								key.isValid(), events);
					}
					setMovieHasSubtitles(movieFile, moviePathDir,
							movieBaseFileName);
					key.reset();
				}
			} catch (ClosedWatchServiceException e) {
				LOGGER.error("fileWatcher closed", e);
			} catch (InterruptedException e) {
				LOGGER.debug("Thread cancelled", e);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
	}
}
