package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SubtitlesTest {

    @Test
    public void should_get_subtitles_convert_download_link_to_https() throws Exception {
        // given
        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("SubDownloadsCnt", "432")
                .put("SubFileName", "Blackout.1x03.HDTV.x264-FoV.srt")
                .put("SubDownloadLink", "http://dl.opensubtitles.org/en/download/src-api/vrf-19b50c54/sid-mlam5ogdeduc5d7a11o55pp443/filead/1953545173.gz")
                .build();
        SearchSubtitlesResult searchSubtitlesResult = new SearchSubtitlesResult(map, "source");
        // when
        Subtitles subtitles = new Subtitles(searchSubtitlesResult);
        // then
        assertThat(subtitles.getDownloadLink()).isEqualTo(
                "https://api.opensubtitles.org/en/download/src-api/vrf-19b50c54/sid-mlam5ogdeduc5d7a11o55pp443/filead/1953545173.gz");
    }
}
