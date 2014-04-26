package pl.rafalmag.subtitledownloader.themoviedb;

import com.omertron.themoviedbapi.model.MovieDb;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class TheMovieDbHelperTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TheMovieDbHelperTest.class);

	@Test
	public void should_hide_apiKey_from_logs() throws Exception {
		// given
		PrintStream out = System.out;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos));

			// when
			LOGGER.warn("api hidden = {}", TheMovieDbHelper.API_KEY);

			// then
			assertThat(baos.toString(),
					containsString("api hidden = [API-KEY]"));
			assertThat(baos.toString(),
					not(containsString(TheMovieDbHelper.API_KEY)));
		} finally {
			System.setOut(out);
		}
	}

	@Test
	public void should_get_movieDb_with_imdb() throws Exception {
		// given
		String title = "Star Wars New Hope";

		// when
		List<MovieDb> searchMovie = TheMovieDbHelper.getInstance().searchMovie(
				title);
		MovieDb firstMovieDb = searchMovie.get(0);
		String imdbID = firstMovieDb.getImdbID();

		// then
		assertThat(firstMovieDb.getTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getOriginalTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getId(), equalTo(11));
		assertThat(imdbID, equalTo("tt0076759"));
	}
}
