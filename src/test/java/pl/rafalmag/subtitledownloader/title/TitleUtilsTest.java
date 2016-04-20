package pl.rafalmag.subtitledownloader.title;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rafalmag.subtitledownloader.utils.ProgressCallbackDummy;

@RunWith(JUnitParamsRunner.class)
public class TitleUtilsTest {

	private static final int TIMEOUT_MS = 10000;

	@Parameters({ "tt1234,1234", "123,123" })
	@Test
	public void should_get_int_from_imdb_string(String imdbStr, int expectedImdb) {
		// given

		// when
		int imdbFromString = TitleUtils.getImdbFromString(imdbStr);

		// then
		assertThat(imdbFromString, equalTo(expectedImdb));
	}

	@Test
	public void should_get_title_for_not_exact_title() throws Exception {
		// given
		String titleWithError = "The Girl With The";
		TitleUtils titleUtils = new TitleUtils(new File("ble"), TIMEOUT_MS,
				new ProgressCallbackDummy());
		// when
		List<Movie> titles = titleUtils.getByTitle(titleWithError);

		// then
		assertThat(titles, not(hasSize(0)));
		Movie firstMovie = titles.get(0);

		// a -> the
		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("The Girl With the Dragon Tattoo"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo(1568346));
	}

	@Test
	public void should_get_title_for_movie_by_title() throws Exception {
		// given
		String titleWithError = "A Lonely Place To Die";
		TitleUtils titleUtils = new TitleUtils(new File("ble"), TIMEOUT_MS,
				new ProgressCallbackDummy());
		// when
		List<Movie> titles = titleUtils.getByTitle(titleWithError);

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.get(0);

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo(1422136));
	}

	// requires existing file
	@Ignore
	@Test
	public void should_get_title_for_movie_by_file_hash() throws Exception {
		// given
		File movieFile = new File(
				"I:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
		TitleUtils titleUtils = new TitleUtils(movieFile, TIMEOUT_MS,
				new ProgressCallbackDummy());
		List<Movie> titles = titleUtils.getByFileHash();

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.get(0);

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo(1422136));
	}

	// requires existing file, but test passes without it - by resolving file
	// name
	// from path
	// @Ignore
	@Test
	public void should_get_title_for_movie_file_combined() throws Exception {
		// given
		File movieFile = new File(
				"H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");

		TitleUtils titleUtils = new TitleUtils(movieFile, TIMEOUT_MS,
				new ProgressCallbackDummy());
		SortedSet<Movie> titles = titleUtils.getTitles();

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.first();

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo(1422136));
	}
}
