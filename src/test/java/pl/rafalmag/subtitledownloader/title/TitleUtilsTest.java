package pl.rafalmag.subtitledownloader.title;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import org.junit.Test;

public class TitleUtilsTest {

	@Test
	public void should_get_title_for_not_exact_title() throws Exception {
		// given
		String titleWithError = "The Girl With A Dragon Tattoo";

		// when
		TitleUtils titleUtils = new TitleUtils(new File("ble"));
		List<Movie> titles = titleUtils.getByTitle(titleWithError);

		// then
		assertThat(titles, not(hasSize(0)));
		Movie firstMovie = titles.get(0);

		// a -> the
		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("The Girl With the Dragon Tattoo"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo("tt1568346"));
	}

	@Test
	public void should_get_title_for_movie_by_title() throws Exception {
		// given
		String titleWithError = "A Lonely Place To Die";

		// when
		TitleUtils titleUtils = new TitleUtils(new File("ble"));
		List<Movie> titles = titleUtils.getByTitle(titleWithError);

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.get(0);

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo("tt1422136"));
	}

	// requires existing file
	// @Ignore
	@Test
	public void should_get_title_for_movie_by_file_hash() throws Exception {
		// given
		File movieFile = new File(
				"H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");

		TitleUtils titleUtils = new TitleUtils(movieFile);
		List<Movie> titles = titleUtils.getByFileHash();

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.get(0);

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo("tt1422136"));
	}

	// requires existing file
	// @Ignore
	@Test
	public void should_get_title_for_movie_file_combined() throws Exception {
		// given
		File movieFile = new File(
				"H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");

		TitleUtils titleUtils = new TitleUtils(movieFile);
		SortedSet<Movie> titles = titleUtils.getTitles(10000);

		// then
		assertThat(titles, hasSize(1));
		Movie firstMovie = titles.first();

		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("A Lonely Place To Die"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo("tt1422136"));
	}
}
