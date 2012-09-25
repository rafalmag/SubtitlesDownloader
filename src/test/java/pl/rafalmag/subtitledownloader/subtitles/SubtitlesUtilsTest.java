package pl.rafalmag.subtitledownloader.subtitles;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import org.junit.Test;

import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

public class SubtitlesUtilsTest {

	private static final int TIMEOUT_MS = 10000;

	@Test
	public void should_get_valid_subtitles() throws Exception {
		// given
		Movie movie = new Movie("Midnight in Paris", 2011, 1605783);
		File movieFile = new File(
				"I:/filmy/!old/Midnight.in.Paris.DVDRip.XviD-TARGET/target-paris-xvid.avi");

		// when
		SubtitlesUtils subtitlesUtils = new SubtitlesUtils(movie, movieFile,
				TIMEOUT_MS, new ProgressCallbackDummy());
		SortedSet<Subtitles> subtitles = subtitlesUtils
				.getSubtitles();

		// then
		List<Subtitles> downloadsOver1000 = select(
				subtitles,
				having(on(Subtitles.class).getDownloadsCount(),
						greaterThan(1000)));
		assertThat(downloadsOver1000, hasSize(greaterThan(1)));
		List<Subtitles> downloadsOver1000WithPropperTitle = select(
				downloadsOver1000,
				having(on(Subtitles.class).getFileName(),
						containsString("Midnight.in.Paris.DVDRip.XviD")));
		assertThat(downloadsOver1000WithPropperTitle, hasSize(greaterThan(1)));

	}
}
