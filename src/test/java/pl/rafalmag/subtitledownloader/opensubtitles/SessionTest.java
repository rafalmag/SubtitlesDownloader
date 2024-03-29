package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.inject.Guice;
import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.TestGuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;
import pl.rafalmag.subtitledownloader.title.Movie;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionTest {

    @Inject
    private Session session;

    @Before
    public void initAndLogin() throws SessionException {
        Guice.createInjector(new TestGuiceModule()).injectMembers(this);
    }

    @After
    public void logout() {
        session.logout();
    }

    @Test
    public void should_get_subtitles_for_movie_by_movieHash_movieByteSize()
            throws Exception {
        // given
        String movieHash = "673243a1f2823a82";
        Long movieByteSize = 1_181_200_270L;
        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = session.searchSubtitlesBy(movieHash, movieByteSize);

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getTitle().equalsIgnoreCase("The Girl With The Dragon Tattoo"),
                        "contains movie with The Girl With The Dragon Tattoo title"));
    }

    @Test
    public void should_get_subtitles_for_movie_by_imdb() throws Exception {
        // given
        int imdb = 1568346;
        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = session.searchSubtitlesBy(imdb);

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
        String title = "The Girl With The Dragon Tattoo";
        // when
        Collection<SearchSubtitlesResult> checkMovieHash2Entities = session.searchSubtitlesBy(title);

        // then
        assertThat(checkMovieHash2Entities).isNotEmpty();

        assertThat(checkMovieHash2Entities).areAtLeast(1,
                new Condition<>(searchSubtitlesResult ->
                        searchSubtitlesResult.getTitle().equalsIgnoreCase("The Girl With The Dragon Tattoo"),
                        "contains movie with The Girl With The Dragon Tattoo title"));
    }

    @Test
    public void should_get_sub_languages() throws Exception {
        // given
        // when
        List<SubtitleLanguage> subtitleLanguages = session.getSubLanguages();
        // then
        SubtitleLanguage english = new SubtitleLanguage("eng", "English", "en");
        SubtitleLanguage polish = new SubtitleLanguage("pol", "Polish", "pl");
        assertThat(subtitleLanguages).contains(english, polish);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void should_guess_movie_from_file_name() throws Exception {
        // given
        String fileName = "Aliens 1080p BluRay AC3 x264-ETRG.mkv";
        // when
        Optional<Movie> movieOptional = session.guessMovieFromFileName(fileName);
        // then
        assertThat(movieOptional).isPresent();
        Movie movie = movieOptional.get();
        assertThat(movie.getImdbId()).isEqualTo(90605);
        assertThat(movie.getTitle()).isEqualToIgnoringCase("Aliens");
        assertThat(movie.getYear()).isEqualTo(1986);
    }
}
