package pl.rafalmag.subtitledownloader;

import java.io.File;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
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

	@Test
	public void should_get_title_info_for_movie() throws Exception {
		// given

		File movieFile = new File(
				"C:/torrents/old/A Lonely Place To Die  {2011} DVDRIP. Jaybob/A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi");
		CheckMovie getTitle = new CheckMovie(session, movieFile);

		// when
		Collection<CheckMovieHash2Entity> checkMovieHash2Entities = getTitle
				.getTitleInfo();

		// then
		// TODO
		// Assert.assertThat(checkMovieHash2Entities,
		// Matchers.hasItem(elementMatcher)("A Lonely Place To Die"));
	}
}
