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
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;

public class CheckMovieSubtitlesTest {

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
	public void should_get_subtitles_for_movie_by_imdb() throws Exception {
		// given
		CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
				session, new Movie(
						"The Girl With The Dragon Tattoo", 2011, 1568346,
						new File("")));

		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
				.getSubtitlesByImdb();

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
	public void should_get_subtitles_for_movie_by_title() throws Exception {
		// given
		CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
				session, new Movie(
						"The Girl With The Dragon Tattoo", 2011, 1568346,
						new File("")));

		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
				.getSubtitlesByTitle();

		// then
		assertThat(checkMovieHash2Entities, not(hasSize(0)));

		List<SearchSubtitlesResult> select2 = select(
				checkMovieHash2Entities,
				having(on(SearchSubtitlesResult.class).getIDMovieImdb(),
						equalTo(1568346)));

		assertThat("Result should has item with imdb: 1568346", select2,
				not(hasSize(0)));
	}

	// requires existing file
	@Ignore
	@Test
	public void should_get_subtitles_for_movie() throws Exception {
		// given
		File movieFile = new File(
				"C:/torrents/!old/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW/The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW.avi");
		CheckMovieSubtitles checkMovieSubtitles = new CheckMovieSubtitles(
				session, new Movie(
						"The Girl With The Dragon Tattoo", 2011, 1568346,
						movieFile));

		long timeoutMs = 10000L;
		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = checkMovieSubtitles
				.getSubtitles(timeoutMs);

		// then
		assertThat(checkMovieHash2Entities, not(hasSize(0)));

		List<SearchSubtitlesResult> select2 = select(
				checkMovieHash2Entities,
				having(on(SearchSubtitlesResult.class).getIDMovieImdb(),
						equalTo(1568346)));

		assertThat("Result should has item with imdb: 1568346", select2,
				not(hasSize(0)));

	}

}
