package pl.rafalmag.subtitledownloader.subtitles;

import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.GuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

import javax.inject.Inject;
import java.io.File;
import java.util.SortedSet;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SubtitlesServiceTest {

    private static final int TIMEOUT_MS = 10_000;

    @Inject
    private SubtitlesService subtitlesService;

    @Before
    public void init() throws SessionException {
        Guice.createInjector(new GuiceModule(null)).injectMembers(this);
    }

    // this test requires a big file in specified path
    @Ignore
    @Test
    public void should_get_valid_subtitles() throws Exception {
        // given
        Movie movie = new Movie("Dead Snow", 2009, 1278340);
        File movieFile = new File(
                "E:/filmy/!old/Dead.Snow.(Doed.Snoe).2009.1080p.BluRay.x264.anoXmous/Dead.Snow.(Doed.Snoe).2009.1080p.BluRay.x264.anoXmous_.mp4");

        // when
        SortedSet<Subtitles> subtitles = subtitlesService.getSubtitles(movie, movieFile, TIMEOUT_MS,
                new ProgressCallbackDummy());

        // then
        assertThat(subtitles.first().getFileName(), equalTo("Doed.Snoe.2009.NORWEGIAN.DVDRip.XviD-DnB.srt"));
    }
}
