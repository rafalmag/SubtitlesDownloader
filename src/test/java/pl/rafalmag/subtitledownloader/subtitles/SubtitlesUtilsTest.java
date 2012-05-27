package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.List;

import org.junit.Test;

import pl.rafalmag.subtitledownloader.title.Movie;

public class SubtitlesUtilsTest {

	@Test
	public void should_get_valid_subtitles() throws Exception {
		// given
		Movie movie = new Movie("A Lonely Place To Die", 2011, 1422136);
		File movieFile = new File(
				"H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");

		// when
		SubtitlesUtils subtitlesUtils = new SubtitlesUtils(movie, movieFile);
		List<Subtitles> subtitles = subtitlesUtils.getSubtitles();

		// then

	}
}
