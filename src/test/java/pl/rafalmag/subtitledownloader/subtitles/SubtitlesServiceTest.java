package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.base.Charsets;
import com.google.inject.Guice;
import org.apache.commons.codec.binary.Base64;
import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.TestGuiceModule;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedSet;
import java.util.zip.InflaterOutputStream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SubtitlesServiceTest {

    private static final int TIMEOUT_MS = 10_000;

    @Inject
    private SubtitlesService subtitlesService;

    @Before
    public void init() throws SessionException {
        Guice.createInjector(new TestGuiceModule()).injectMembers(this);
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

    @Test
    public void should_calculate_md5() throws Exception {
        // given
        File tempFile = Files.newTemporaryFile();
        tempFile.deleteOnExit();
        com.google.common.io.Files.asCharSink(tempFile, Charsets.UTF_8).write("test");
        // when
        String md5 = subtitlesService.md5(tempFile);
        // then
        assertThat(md5, equalTo("098f6bcd4621d373cade4e832627b4f6"));
    }

    @Test
    public void should_gzip_file() throws Exception {
        // given
        File tempFile = Files.newTemporaryFile();
        tempFile.deleteOnExit();
        com.google.common.io.Files.asCharSink(tempFile, Charsets.UTF_8).write("test");
        // when
        String gzip = subtitlesService.gzip(tempFile);
        // then
        assertThat(gzip, equalTo("eJwrSS0uAQAEXQHB"));
        assertTrue(Base64.isBase64(gzip));
        byte[] decoded = Base64.decodeBase64(gzip);
        String decompressedString = decompress(decoded);
        assertThat(decompressedString, equalTo("test"));
    }

    private String decompress(byte[] byteArray) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (OutputStream os = new InflaterOutputStream(byteArrayOutputStream)) {
            os.write(byteArray);
        }
        return byteArrayOutputStream.toString();
    }
}
