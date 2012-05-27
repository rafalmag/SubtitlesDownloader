package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TitleUtils.class);

	private final File movieFile;

	public TitleUtils(File movieFile) {
		this.movieFile = movieFile;
	}

	public SortedSet<Movie> getTitles(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		String fileBaseName = FilenameUtils.getBaseName(movieFile.getName());
		SortedSet<Movie> set = Sets.newTreeSet(new Comparator<Movie>() {

			@Override
			public int compare(Movie o1, Movie o2) {
				// TODO move it, enhance it
				return o1.getTitle().compareTo(o2.getTitle());
			}

		});
		String title = TitleNameUtils.getTitleFrom(fileBaseName);
		startTasksAndGetResults(timeoutMs, title, set);

		return set;
	}

	private void startTasksAndGetResults(long timeoutMs, final String title,
			SortedSet<Movie> set) throws InterruptedException {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			CompletionService<List<Movie>> compService = new ExecutorCompletionService<List<Movie>>(
					newFixedThreadPool);
			compService.submit(new Callable<List<Movie>>() {

				@Override
				public List<Movie> call() {
					return getByTitle(title);
				}
			});
			compService.submit(new Callable<List<Movie>>() {

				@Override
				public List<Movie> call() throws SubtitlesDownloaderException {
					return getByFileHash();
				}
			});

			long startWaitingMs = System.currentTimeMillis();
			try {
				set.addAll(compService.take().get());
			} catch (ExecutionException e) {
				LOGGER.error("Could not get XXX", e);
			}
			long waitingTookMs = System.currentTimeMillis() - startWaitingMs;
			long timeLeftMs = timeoutMs - waitingTookMs;
			if (timeLeftMs > 0) {
				Future<List<Movie>> poll = compService.poll(timeoutMs
						- waitingTookMs, TimeUnit.MILLISECONDS);
				if (poll == null) {
					LOGGER.warn("Could not XXX get in given time");
				} else {
					try {
						set.addAll(poll.get());
					} catch (ExecutionException e) {
						LOGGER.error("Could not get XXX", e);
					}
				}
			}
		} finally {
			newFixedThreadPool.shutdown();
		}
	}

	protected List<Movie> getByTitle(String title) {
		List<MovieDb> searchMovie = TheMovieDbHelper.getInstance().searchMovie(
				title);
		List<Movie> list = Lists.transform(searchMovie,
				new Function<MovieDb, Movie>() {

					@Override
					public Movie apply(MovieDb input) {
						return new Movie(input);
					}

				});
		return list;
	}

	protected List<Movie> getByFileHash() throws SubtitlesDownloaderException {
		Session session = new Session();
		session.login();
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		List<CheckMovieHash2Entity> checkMovieHash2Entities = checkMovie
				.getTitleInfo();

		List<Movie> list = Lists.transform(checkMovieHash2Entities,
				new Function<CheckMovieHash2Entity, Movie>() {

					@Override
					public Movie apply(CheckMovieHash2Entity input) {
						return new Movie(input);
					}

				});
		return list;
	}

}
