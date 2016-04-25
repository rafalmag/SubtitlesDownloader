package pl.rafalmag.subtitledownloader.themoviedb;

import com.google.inject.Guice;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.TestGuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class TheMovieDbServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbServiceTest.class);

    @Inject
    private TheMovieDbService theMovieDbService;

    @Before
    public void init() throws SessionException {
        Guice.createInjector(new TestGuiceModule()).injectMembers(this);
    }

    @Test
    public void should_hide_apiKey_from_logs() throws Exception {
        // given
        PrintStream out = System.out;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));

            // when
            LOGGER.warn("api hidden = {}", TheMovieDbService.API_KEY);

            // then
            assertThat(baos.toString(),
                    containsString("api hidden = [API-KEY]"));
            assertThat(baos.toString(),
                    not(containsString(TheMovieDbService.API_KEY)));
        } finally {
            System.setOut(out);
        }
    }

    @Test
    public void should_get_movieDb_with_imdb() throws Exception {
        // given
        String title = "Star Wars New Hope";
        Locale.setDefault(Locale.ENGLISH);

        // when
        List<MovieInfo> searchMovie = theMovieDbService.searchMovie(title);
        MovieInfo firstMovieDb = searchMovie.get(0);
        String imdbID = firstMovieDb.getImdbID();

        // then
        assertThat(firstMovieDb.getTitle(),
                startsWith("Star Wars"));
        assertThat(firstMovieDb.getOriginalTitle(),
                startsWith("Star Wars"));
        assertThat(firstMovieDb.getId(), equalTo(11));
        assertThat(imdbID, equalTo("tt0076759"));
    }
}
