package pl.rafalmag.subtitledownloader.title;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.omertron.themoviedbapi.model.MovieDb;
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

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
		Collection<? extends Callable<List<Movie>>> solvers = ImmutableList.of(
				new NamedCallable<>("-movByTitle", () -> getByTitle(title)),
				new NamedCallable<>("-movByFileHash", this::getByFileHash)
		);
		Collection<List<Movie>> solve = Utils.solve(EXECUTOR,
				solvers, timeoutMs, progressCallback);

		return StreamSupport.stream(solve.spliterator(), false)
				.flatMap(i -> StreamSupport.stream(i.spliterator(), false))
				.collect(Collectors.toCollection(TreeSet::new));
	}

	protected List<Movie> getByTitle(String title) {
		List<MovieDb> searchMovie = TheMovieDbHelper.getInstance().searchMovie(
				title);
		List<Movie> list = Lists.transform(searchMovie, Movie::new);
		LOGGER.debug("TheMovieDb returned: {}", list);
		return list;
	}

	protected List<Movie> getByFileHash() throws SubtitlesDownloaderException {
		Session session = new Session();
		session.login(); // mandatory
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		List<MovieEntity> checkMovieHash2Entities = checkMovie.getTitleInfo();

		return Lists.transform(checkMovieHash2Entities, Movie::new);
	}
}