package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtils {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TitleUtils.class);

	private static Pattern IMDB_PATTERN = Pattern.compile("\\D*(\\d+).*");

	public static int getImdbFromString(@Nullable String imdbStr) {
		if (Strings.isNullOrEmpty(imdbStr)) {
			LOGGER.debug("imdbId is null");
			return -1;
		}
		Matcher matcher = IMDB_PATTERN.matcher(imdbStr);
		if (matcher.find()) {
			String digits = matcher.group(1);
			return Integer.parseInt(digits);
		} else {
			LOGGER.warn("imdbId (" + imdbStr + ") doesn't match "
					+ IMDB_PATTERN);
			return -1;
		}
	}

	private final File movieFile;

	private final long timeoutMs;

	private final ProgressCallback progressCallback;

	public TitleUtils(File movieFile, long timeoutMs,
			ProgressCallback progressCallback) {
		this.movieFile = movieFile;
		this.timeoutMs = timeoutMs;
		this.progressCallback = progressCallback;
	}

	public SortedSet<Movie> getTitles()
			throws InterruptedException {
		String title = TitleNameUtils.getTitleFrom(movieFile.getName());
		return startTasksAndGetResults(title);
	}

	private final static ExecutorService EXECUTOR = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
					.namingPattern("Title-%d").build());

	private SortedSet<Movie> startTasksAndGetResults(final String title)
			throws InterruptedException {
		SortedSet<Movie> set = Sets.newTreeSet();
		Collection<? extends Callable<List<Movie>>> solvers = ImmutableList.of(
				new NamedCallable<>("-movByTitle", new Callable<List<Movie>>() {

					@Override
					public List<Movie> call() {
						return getByTitle(title);
					}
				}), new NamedCallable<>("-movByFileHash",
						new Callable<List<Movie>>() {

							@Override
							public List<Movie> call()
									throws SubtitlesDownloaderException {
								return getByFileHash();
							}
						})
				);
		Collection<List<Movie>> solve = Utils.solve(EXECUTOR,
				solvers, timeoutMs, progressCallback);

		for (List<Movie> item : solve) {
			set.addAll(item);
		}
		return set;
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
		LOGGER.debug("TheMovieDb returned: {}", list);
		return list;
	}

	protected List<Movie> getByFileHash() throws SubtitlesDownloaderException {
		Session session = new Session();
		session.login(); // mandatory
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		List<MovieEntity> checkMovieHash2Entities = checkMovie.getTitleInfo();

		List<Movie> list = Lists.transform(checkMovieHash2Entities,
				new Function<MovieEntity, Movie>() {

					@Override
					public Movie apply(MovieEntity input) {
						return new Movie(input);
					}

				});
		return list;
	}
}