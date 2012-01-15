package pl.rafalmag.subtitledownloader;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
	@Deprecated
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void should_get_title_info_for_movie() throws Exception {
		// given
		File movieFile = new File(
				"C:/torrents/old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		Collection<CheckMovieHash2Entity> checkMovieHash2Entities = checkMovie
				.getTitleInfo();

		// then
		assertThat("Result should has few records",
				(Collection) checkMovieHash2Entities, is(not(empty())));
		Collection select = select(
				checkMovieHash2Entities,
				having(on(CheckMovieHash2Entity.class).getMovieName(),
						equalTo("A Lonely Place To Die")));
		assertThat("Result should has item with title A Lonely Place To Die",
				select, is(not(empty())));
	}

	@Test
	public void should_get_subtitles_for_movie() throws Exception {
		// given
		File movieFile = new File(
				"C:/torrents/old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovie
				.getSubtitles();

		// then
		assertThat((Collection) checkMovieHash2Entities, is(not(empty())));
	}

	@Test
	public void should_calculate_hash() throws Exception {
		// given
		URL resource = this.getClass().getResource("/breakdance.avi");
		File file = new File(resource.toURI());
		// when
		String hashcode = new CheckMovie(session, file).getHashCode();
		// then
		assertThat(hashcode, Matchers.equalTo("8e245d9679d31e12"));
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
