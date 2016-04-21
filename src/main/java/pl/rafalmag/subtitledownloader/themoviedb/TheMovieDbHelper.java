package pl.rafalmag.subtitledownloader.themoviedb;

import com.google.common.base.Throwables;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.movie.MovieInfo;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TheMovieDbHelper {

    // TODO should be true, see reported issue : https://www.themoviedb.org/talk/57194db392514176a4000156
    public static final boolean INCLUDE_ADULT = false;

    private static class TheMovieDbHelperHolder {
        private final static TheMovieDbHelper instance = new TheMovieDbHelper();
    }

    public static TheMovieDbHelper getInstance() {
        return TheMovieDbHelperHolder.instance;
    }

    public static final String API_KEY = "d59492cb5d91e31ca1832ce5c447a099";

    private final TheMovieDbApi theMovieDb;

    private TheMovieDbHelper() {
        try {
            theMovieDb = new TheMovieDbApi(API_KEY);
        } catch (MovieDbException e) {
            throw Throwables.propagate(e);
        }
    }

    public List<MovieInfo> searchMovie(String title) {
        try {
            List<MovieInfo> searchMovie = theMovieDb.searchMovie(title, 0,
                    Locale.getDefault().getLanguage(), INCLUDE_ADULT, 0, 0, null)
                    .getResults();
            return searchMovie.stream().map(MovieDbLazyImdb::new).collect(Collectors.toList());
        } catch (MovieDbException e) {
            throw new IllegalStateException("Could not get list of movies for title " + title +
                    ", because of " + e.getMessage(), e);
        }
    }

    public MovieInfo getFullMovieDb(MovieInfo movieDb) throws MovieDbException {
        return getFullMovieDb(movieDb.getId());
    }

    public MovieInfo getFullMovieDb(int movieDbId) throws MovieDbException {
        return theMovieDb.getMovieInfo(movieDbId, Locale.getDefault().getLanguage());
    }

}
