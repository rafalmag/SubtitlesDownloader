package pl.rafalmag.subtitledownloader.themoviedb;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheMovieDbHelperTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TheMovieDbHelperTest.class);

	@Test
	public void should_hide_apiKey_from_logs() throws Exception {
		// given

		// TODO appender
		// when
		LOGGER.warn("api hidden = {}", TheMovieDbHelper.API_KEY);

		// then
		// TODO verify appender

	}
}
