package pl.rafalmag.subtitledownloader.subtitles;

import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

import java.io.File;
import java.util.SortedSet;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SubtitlesUtilsTest {

    private static final int TIMEOUT_MS = 10_000;

    // this test requires a big file in specified path
    @Ignore
    @Test
    public void should_get_valid_subtitles() throws Exception {
        // given
        Movie movie = new Movie("Dead Snow", 2009, 1278340);
        File movieFile = new File(
                "E:/filmy/!old/Dead.Snow.(Doed.Snoe).2009.1080p.BluRay.x264.anoXmous/Dead.Snow.(Doed.Snoe).2009.1080p.BluRay.x264.anoXmous_.mp4");

        // when
        SubtitlesUtils subtitlesUtils = new SubtitlesUtils();
        SortedSet<Subtitles> subtitles = subtitlesUtils.getSubtitles(movie, movieFile,
                TIMEOUT_MS, new ProgressCallbackDummy());

        // then
        assertThat(subtitles.first().getFileName(), equalTo("Doed.Snoe.2009.NORWEGIAN.DVDRip.XviD-DnB.srt"));
    }
}
