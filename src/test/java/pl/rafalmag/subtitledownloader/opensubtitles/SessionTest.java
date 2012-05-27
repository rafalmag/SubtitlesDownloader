package pl.rafalmag.subtitledownloader.opensubtitles;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.rafalmag.subtitledownloader.opensubtitles.entities.ImdbMovieDetails;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

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

	@Deprecated
	@Test
	public void should_get_title_from_imdbId() throws Exception {
		// given
		int imdbId = 1422136;

		// when
		ImdbMovieDetails imdbMovieDetails = session.getImdbMovieDetails(imdbId);

		assertThat(imdbMovieDetails.getTitle(),
				equalTo("A Lonely Place to Die"));
	}

	@Test
	public void should_get_subtitles_for_movie_by_movieHash_movieByteSize()
			throws Exception {
		// given
		String movieHash = "673243a1f2823a82";
		Long movieByteSize = 1_181_200_270L;
		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = session
				.searchSubtitlesBy(movieHash, movieByteSize);

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
	public void should_get_subtitles_for_movie_by_imdb() throws Exception {
		// given
		int imdb = 1568346;
		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = session
				.searchSubtitlesBy(imdb);

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
		String title = "The Girl With The Dragon Tattoo";
		// when
		Collection<SearchSubtitlesResult> checkMovieHash2Entities = session
				.searchSubtitlesBy(title);

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

}
