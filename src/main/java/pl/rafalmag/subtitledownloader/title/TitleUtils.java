package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.Utils;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtils {

	private static Pattern IMDB_PATTERN = Pattern.compile("\\D*(\\d+).*");

	public static int getImdbFromString(String imdbStr) {
		Matcher matcher = IMDB_PATTERN.matcher(imdbStr);
		if (matcher.find()) {
			String digits = matcher.group(1);
			return Integer.parseInt(digits);
		} else {
			throw new IllegalStateException("imdbId (" + imdbStr
					+ ") doesn't match " + IMDB_PATTERN);
		}
	}

	private final File movieFile;

	public TitleUtils(File movieFile) {
		this.movieFile = movieFile;
	}

	public SortedSet<Movie> getTitles(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		String title = TitleNameUtils.getTitleFrom(movieFile.getName());
		return startTasksAndGetResults(timeoutMs, title);
	}

	private SortedSet<Movie> startTasksAndGetResults(long timeoutMs,
			final String title) throws InterruptedException {
		SortedSet<Movie> set = Sets.newTreeSet(new Comparator<Movie>() {

			@Override
			public int compare(Movie o1, Movie o2) {
				// TODO move it, enhance it
				return o1.getTitle().compareTo(o2.getTitle());
			}

		});

		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			Collection<Callable<List<Movie>>> solvers = ImmutableList.of(
					new Callable<List<Movie>>() {

						@Override
						public List<Movie> call() {
							return getByTitle(title);
						}
					}, new Callable<List<Movie>>() {

						@Override
						public List<Movie> call()
								throws SubtitlesDownloaderException {
							return getByFileHash();
						}
					});
			Collection<List<Movie>> solve = Utils.solve(newFixedThreadPool,
					solvers, timeoutMs);

			for (List<Movie> item : solve) {
				set.addAll(item);
			}
			return set;
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
		session.login(); // mandatory
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