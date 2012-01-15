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

	@SuppressWarnings("unchecked")
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
		@SuppressWarnings("rawtypes")
		Collection select = select(
				checkMovieHash2Entities,
				having(on(CheckMovieHash2Entity.class).getMovieName(),
						equalTo("A Lonely Place To Die")));
		assertThat("Result should has item with title A Lonely Place To Die",
				select, is(not(empty())));
	}
}
