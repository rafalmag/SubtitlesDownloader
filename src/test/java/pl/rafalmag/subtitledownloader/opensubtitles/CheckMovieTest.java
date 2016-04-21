package pl.rafalmag.subtitledownloader.opensubtitles;

import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckMovieTest {

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

    @Ignore
    @Test
    public void should_get_title_info_for_movie() throws Exception {
        // given
        File movieFile = new File(
                "H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
        CheckMovie checkMovie = new CheckMovie(session, movieFile);

        // when
        List<MovieEntity> movieEntities = checkMovie.getTitleInfo();

        // then
        assertThat(movieEntities).isNotEmpty();
        assertThat(movieEntities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getTitle().equalsIgnoreCase("The Girl With The Dragon Tattoo"),
                        "contains movie with The Girl With The Dragon Tattoo title"));
    }

    @Ignore
    @Test
    public void should_get_subtitles_for_movie_by_hash_and_size()
            throws Exception {
        // given
        File movieFile = new File(
                "C:/torrents/!old/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW.avi");
        CheckMovie checkMovie = new CheckMovie(session, movieFile);

        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovie
                .getSubtitlesByMovieHashAndByteSize();

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getTitle().equalsIgnoreCase("The Girl With The Dragon Tattoo"),
                        "contains movie with The Girl With The Dragon Tattoo title"));
    }

    @Test
    public void should_calculate_hash() throws Exception {
        // given
        URL resource = this.getClass().getResource("/breakdance.avi");
        File file = new File(resource.toURI());
        // when
        String hashcode = new CheckMovie(session, file).getHashCode();
        // then
        assertThat(hashcode).isEqualTo("8e245d9679d31e12");
    }

    @Test
    public void should_get_byte_size() throws Exception {
        // given
        URL resource = this.getClass().getResource("/breakdance.avi");
        File file = new File(resource.toURI());
        // when
        long fileSize = new CheckMovie(session, file).getByteSize();
        // then
        assertThat(fileSize).isEqualTo(12_909_756L);
    }
}
