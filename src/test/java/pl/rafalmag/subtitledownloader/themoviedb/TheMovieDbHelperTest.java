package pl.rafalmag.subtitledownloader.themoviedb;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheMovieDbHelperTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TheMovieDbHelperTest.class);

	@Test
	public void should_hide_apiKey_from_logs() throws Exception {
		// given
		PrintStream out = System.out;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos));

			// when
			LOGGER.warn("api hidden = {}", TheMovieDbHelper.API_KEY);

			// then
			assertThat(baos.toString(),
					containsString("api hidden = [API-KEY]"));
			assertThat(baos.toString(),
					not(containsString(TheMovieDbHelper.API_KEY)));
		} finally {
			System.setOut(out);
		}
	}
}
