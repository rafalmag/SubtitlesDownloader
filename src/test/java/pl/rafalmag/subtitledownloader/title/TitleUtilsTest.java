package pl.rafalmag.subtitledownloader.title;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TitleUtilsTest {

    @BeforeClass
    public static void initLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Parameters({"tt1234,1234", "123,123"})
    @Test
    public void should_get_int_from_imdb_string(String imdbStr, int expectedImdb) {
        // given

        // when
        int imdbFromString = TitleUtils.getImdbFromString(imdbStr);

        // then
        assertThat(imdbFromString, equalTo(expectedImdb));
    }
}
