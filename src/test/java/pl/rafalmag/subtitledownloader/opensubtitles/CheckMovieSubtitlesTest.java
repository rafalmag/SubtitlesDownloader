package pl.rafalmag.subtitledownloader.opensubtitles;

import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;

import java.io.File;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckMovieSubtitlesTest {

    private Session session;

    @Before
    public void login() throws SessionException {
        session = new Session();
        session.login();
    }

    @After
    public void logout() {
        session.logout();
    }

    @Test
    public void should_get_subtitles_for_movie_by_imdb() throws Exception {
        // given
        CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
                session, new File(""), new Movie(
                "The Girl With The Dragon Tattoo", 2011, 1568346));

        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitlesByImdb();

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
        CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
                session, new File(""), new Movie(
                "The Girl With The Dragon Tattoo", 2011, 1568346));

        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitlesByTitle();

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
        CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
                session, movieFile, new Movie(
                "The Girl With The Dragon Tattoo", 2011, 1568346));

        long timeoutMs = 10000L;
        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
                .getSubtitles(timeoutMs);

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getIDMovieImdb() == 1568346,
                        "Result should have item with imdb: 1568346"));

    }

}
