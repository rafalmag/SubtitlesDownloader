package pl.rafalmag.subtitledownloader.opensubtitles;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

public class CheckMovieTest {

	private Session session;

	@Before
	public void login() throws SessionException {
		session = new Session();
		session.login();
	}

	@After
	public void logout() {
		session.logout();
	}

	@Ignore
	@Test
	public void should_get_title_info_for_movie() throws Exception {
		// given
		File movieFile = new File(
				"H:/filmy/!old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		List<CheckMovieHash2Entity> checkMovieHash2Entities = checkMovie
				.getTitleInfo();

		// then
		assertThat("Result should has few records", checkMovieHash2Entities,
				not(hasSize(0)));
		List<CheckMovieHash2Entity> select = select(
				checkMovieHash2Entities,
				having(on(CheckMovieHash2Entity.class).getMovieName(),
						equalToIgnoringCase("A Lonely Place to Die")));
		assertThat("Result should has item with title: A Lonely Place To Die",
				select, not(hasSize(0)));
	}

	@Ignore
	@Test
	public void should_get_subtitles_for_movie_by_hash_and_size()
			throws Exception {
		// given
		File movieFile = new File(
				"C:/torrents/!old/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW.avi");
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovie
				.getSubtitlesByMovieHashAndByteSize();

		// then
		assertThat(checkMovieHash2Entities, not(hasSize(0)));

		List<SearchSubtitlesResult> select2 = select(
				checkMovieHash2Entities,
				having(on(SearchSubtitlesResult.class).getTitle(),
						equalToIgnoringCase("The Girl With The Dragon Tattoo")));

		assertThat(
				"Result should has item with title: The Girl With The Dragon Tattoo",
				select2, not(hasSize(0)));
	}

	@Test
	public void should_calculate_hash() throws Exception {
		// given
		URL resource = this.getClass().getResource("/breakdance.avi");
		File file = new File(resource.toURI());
		// when
		String hashcode = new CheckMovie(session, file).getHashCode();
		// then
		assertThat(hashcode, equalTo("8e245d9679d31e12"));
	}

	@Test
	public void should_get_byte_size() throws Exception {
		// given
		URL resource = this.getClass().getResource("/breakdance.avi");
		File file = new File(resource.toURI());
		// when
		long fileSize = new CheckMovie(session, file).getByteSize();
		// then
		assertThat(fileSize, equalTo(12_909_756L));

	}
}
