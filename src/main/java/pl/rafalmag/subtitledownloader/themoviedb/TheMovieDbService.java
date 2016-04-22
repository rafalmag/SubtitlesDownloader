package pl.rafalmag.subtitledownloader.themoviedb;

import com.google.common.base.Throwables;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.movie.MovieInfo;

import javax.inject.Singleton;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Singleton
public class TheMovieDbService {

    public static final boolean INCLUDE_ADULT = true;

    public static final String API_KEY = "d59492cb5d91e31ca1832ce5c447a099";

    private final TheMovieDbApi theMovieDb;

    public TheMovieDbService() {
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
            return searchMovie.stream().map(movieInfo -> new MovieDbLazyImdb(movieInfo, this)).collect(Collectors.toList());
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
