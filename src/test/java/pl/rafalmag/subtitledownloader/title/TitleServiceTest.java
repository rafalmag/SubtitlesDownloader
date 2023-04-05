package pl.rafalmag.subtitledownloader.title;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.TestGuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;
import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TitleServiceTest {

    @Inject
    ActorSystem system;
    private static final int TIMEOUT_MS = 10000;

    @BeforeClass
    public static void initLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }


    @Inject
    TitleService titleService;

    @Before
    public void init() throws SessionException {
        Guice.createInjector(new TestGuiceModule()).injectMembers(this);
    }

    @Test
    public void should_get_title_for_not_exact_title() throws Exception {
        // given
        String titleWithError = "The Girl With The Dragon";
        // when
        List<Movie> titles = titleService.getByTitle(titleWithError)
                .runWith(Sink.seq(), system)
                .toCompletableFuture().get(5, TimeUnit.SECONDS);

        // then
        assertThat(titles, not(hasSize(0)));
        Movie firstMovie = titles.get(1);

        // a -> the
        assertThat(firstMovie.getTitle(), equalToIgnoringCase("The Girl With the Dragon Tattoo"));
        assertThat(firstMovie.getYear(), equalTo(2011));
        assertThat(firstMovie.getImdbId(), equalTo(1568346));
    }

    @Test
    public void should_get_title_for_movie_by_title() throws Exception {
        // given
        String titleWithError = "A Lonely Place To Die";
        // when
        List<Movie> titles = titleService.getByTitle(titleWithError)
                .runWith(Sink.seq(), system)
                .toCompletableFuture().get(5, TimeUnit.SECONDS);

        // then
        Movie firstMovie = titles.get(0);

        assertThat(firstMovie.getTitle(), equalToIgnoringCase("A Lonely Place To Die"));
        assertThat(firstMovie.getYear(), equalTo(2011));
        assertThat(firstMovie.getImdbId(), equalTo(1422136));
    }

    // requires existing file
    @Ignore
    @Test
    public void should_get_title_for_movie_by_file_hash() throws Exception {
        // given
        File movieFile = new File(
                "I:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
        // when
        List<Movie> titles = titleService.getByFileHash(movieFile)
                .runWith(Sink.seq(), system)
                .toCompletableFuture().get(3, TimeUnit.SECONDS);

        // then
        assertThat(titles, hasSize(1));
        Movie firstMovie = titles.get(0);

        assertThat(firstMovie.getTitle(), equalToIgnoringCase("A Lonely Place To Die"));
        assertThat(firstMovie.getYear(), equalTo(2011));
        assertThat(firstMovie.getImdbId(), equalTo(1422136));
    }

    // requires existing file, but test passes without it - by resolving file
    // name
    // from path
    // @Ignore
    @Test
    public void should_get_title_for_movie_file_combined() throws Exception {
        // given
        File movieFile = new File(
                "H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
        // when
        Set<Movie> titles = titleService.getTitles(movieFile, TIMEOUT_MS, new ProgressCallbackDummy());

        // then
        Movie firstMovie = titles.stream().toList().get(1);

        assertThat(firstMovie.getTitle(), equalToIgnoringCase("A Lonely Place To Die"));
        assertThat(firstMovie.getYear(), equalTo(2011));
        assertThat(firstMovie.getImdbId(), equalTo(1422136));
    }

    @Test
    public void should_get_title_for_movie_by_file_name() throws Exception {
        // given
        File movieFile = new File(
                "X:/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
        // when
        List<Movie> titles = titleService.getByFileName(movieFile)
                .runWith(Sink.seq(), system)
                .toCompletableFuture().get(3, TimeUnit.SECONDS);

        // then
        assertThat(titles, hasSize(1));
        Movie firstMovie = titles.get(0);

        assertThat(firstMovie.getTitle(), equalToIgnoringCase("A Lonely Place To Die"));
        assertThat(firstMovie.getYear(), equalTo(2011));
        assertThat(firstMovie.getImdbId(), equalTo(1422136));
    }
}
