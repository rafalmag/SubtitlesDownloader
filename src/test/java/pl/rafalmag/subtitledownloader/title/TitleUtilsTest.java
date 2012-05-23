package pl.rafalmag.subtitledownloader.title;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtilsTest {

	@Test
	public void should_get_year() throws Exception {
		// given
		MovieDb movieDb = new MovieDb();
		movieDb.setReleaseDate("1977-05-25");
		int expectedYear = 1977;

		// when
		int year = TitleUtils.getYear(movieDb);

		// then
		assertThat(year, equalTo(expectedYear));
	}

	@Test
	public void should_get_not_exact_title() throws Exception {
		// given
		File movieFile = new File("C:/The Girl With A Dragon Tattoo.avi");

		// when
		TitleUtils titleUtils = new TitleUtils(movieFile);
		List<Movie> titles = titleUtils.getTitles();

		// then
		assertThat(titles, not(hasSize(0)));
		Movie firstMovie = titles.get(0);

		// a -> the
		assertThat(firstMovie.getTitle(),
				equalToIgnoringCase("The Girl With the Dragon Tattoo"));
		assertThat(firstMovie.getYear(), equalTo(2011));
		assertThat(firstMovie.getImdbId(), equalTo("tt1568346"));
	}
}
