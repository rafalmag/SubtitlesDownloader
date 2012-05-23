package pl.rafalmag.subtitledownloader.themoviedb;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.moviejukebox.themoviedb.TheMovieDb;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TheMovieDbHelper {

	private static class TheMovieDbHelperHolder {
		private final static TheMovieDbHelper instance = new TheMovieDbHelper();
	}

	public static TheMovieDbHelper getInstance() {
		return TheMovieDbHelperHolder.instance;
	}

	private static final String LANGUAGE = "english";
	public static final String API_KEY = "d59492cb5d91e31ca1832ce5c447a099";

	private final TheMovieDb theMovieDb;

	private TheMovieDbHelper() {
		try {
			theMovieDb = new TheMovieDb(API_KEY);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public List<MovieDb> searchMovie(String title) {
		List<MovieDb> searchMovie = theMovieDb.searchMovie(title, LANGUAGE,
				true);
		return Lists.transform(searchMovie, new Function<MovieDb, MovieDb>() {

			@Override
			public MovieDb apply(MovieDb input) {
				return new MovieDbLazyImdb(input);
			}
		});
	}

	public MovieDb getFullMovieDb(MovieDb movieDb) {
		return getFullMovieDb(movieDb.getId());
	}

	public MovieDb getFullMovieDb(int movieDbId) {
		return theMovieDb.getMovieInfo(movieDbId, LANGUAGE);
	}

}
