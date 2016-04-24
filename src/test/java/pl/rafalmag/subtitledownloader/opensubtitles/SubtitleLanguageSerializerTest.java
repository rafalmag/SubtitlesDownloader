package pl.rafalmag.subtitledownloader.opensubtitles;

import org.junit.Test;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;

import static org.assertj.core.api.Assertions.assertThat;

public class SubtitleLanguageSerializerTest {

    @Test
    public void should_serialize() throws Exception {
        //given
        SubtitleLanguage subtitleLanguage = new SubtitleLanguage("eng", "English", "en");
        //when
        SubtitleLanguageSerializer subtitleLanguageSerializer = new SubtitleLanguageSerializer();
        String serialized = subtitleLanguageSerializer.toString(subtitleLanguage);
        // then
        assertThat(serialized).isEqualTo("{\"id\":\"eng\",\"languageName\":\"English\",\"isoCode\":\"en\"}");
    }

    @Test
    public void should_deserialize() throws Exception {
//given
        String serialized = "{\"id\":\"eng\",\"languageName\":\"English\",\"isoCode\":\"en\"}";
        //when
        SubtitleLanguageSerializer subtitleLanguageSerializer = new SubtitleLanguageSerializer();
        SubtitleLanguage actualSubtitleLanguage = subtitleLanguageSerializer.fromString(serialized);
        // then
        assertThat(actualSubtitleLanguage).isEqualTo(new SubtitleLanguage("eng", "English", "en"));

    }

}