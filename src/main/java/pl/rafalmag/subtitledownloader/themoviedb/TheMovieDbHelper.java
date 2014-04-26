package pl.rafalmag.subtitledownloader.themoviedb;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;

import java.util.List;

public class TheMovieDbHelper {

	private static class TheMovieDbHelperHolder {
		private final static TheMovieDbHelper instance = new TheMovieDbHelper();
	}

	public static TheMovieDbHelper getInstance() {
		return TheMovieDbHelperHolder.instance;
	}

	private static final String LANGUAGE = "english";
	public static final String API_KEY = "d59492cb5d91e31ca1832ce5c447a099";

	private final TheMovieDbApi theMovieDb;

	private TheMovieDbHelper() {
		try {
			theMovieDb = new TheMovieDbApi(API_KEY);
		} catch (MovieDbException e) {
			throw Throwables.propagate(e);
		}
	}

	public List<MovieDb> searchMovie(String title) {
		List<MovieDb> searchMovie = null;
		try {
			searchMovie = theMovieDb.searchMovie(title, 0, LANGUAGE,
					true, 0).getResults();
		} catch (MovieDbException e) {
			throw new IllegalStateException("Could not get list of movies for title " + title +
					", because of " + e.getMessage(), e);
		}

		return Lists.transform(searchMovie, new Function<MovieDb, MovieDb>() {

			@Override
			public MovieDb apply(MovieDb input) {
				return new MovieDbLazyImdb(input);
			}
		});
	}

	public MovieDb getFullMovieDb(MovieDb movieDb) throws MovieDbException {
		return getFullMovieDb(movieDb.getId());
	}

	public MovieDb getFullMovieDb(int movieDbId) throws MovieDbException {
		return theMovieDb.getMovieInfo(movieDbId, LANGUAGE);
	}

}
