package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.inject.Guice;
import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.GuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckMovieSubtitlesTest {

    @Inject
    private Session session;

    @Inject
    private CheckMovieSubtitles checkMovieSubtitles;

    @Before
    public void initAndLogin() throws SessionException {
        Guice.createInjector(new GuiceModule(() -> null)).injectMembers(this);
        session.login();
    }

    @After
    public void logout() {
        session.logout();
    }

    @Test
    public void should_get_subtitles_for_movie_by_imdb() throws Exception {
        // given

        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitlesByImdb(new Movie("The Girl With The Dragon Tattoo", 2011, 1568346));

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getTitle().equalsIgnoreCase("The Girl With The Dragon Tattoo"),
                        "contains movie with The Girl With The Dragon Tattoo title"));
    }

    @Test
    public void should_get_subtitles_for_movie_by_title() throws Exception {
        // given

        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitlesByTitle(new Movie("The Girl With The Dragon Tattoo", 2011, 1568346));

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getIDMovieImdb() == 1568346,
                        "Result should have item with imdb: 1568346"));
    }

    // requires existing file
    @Ignore
    @Test
    public void should_get_subtitles_for_movie() throws Exception {
        // given
        File movieFile = new File(
                "C:/torrents/!old/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW.avi");

        long timeoutMs = 10000L;
        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitles(new Movie("The Girl With The Dragon Tattoo", 2011, 1568346), movieFile, timeoutMs);

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getIDMovieImdb() == 1568346,
                        "Result should have item with imdb: 1568346"));

    }

}
