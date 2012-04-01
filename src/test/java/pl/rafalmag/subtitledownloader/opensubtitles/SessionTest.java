package pl.rafalmag.subtitledownloader.opensubtitles;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.rafalmag.subtitledownloader.opensubtitles.entities.ImdbMovieDetails;

public class SessionTest {

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
	public void should_get_title_from_imdbId() throws Exception {
		// given
		int imdbId = 1422136;

		// when
		ImdbMovieDetails imdbMovieDetails = session.getImdbMovieDetails(imdbId);

		assertThat(imdbMovieDetails.getTitle(),
				equalTo("A Lonely Place to Die"));
	}

}
