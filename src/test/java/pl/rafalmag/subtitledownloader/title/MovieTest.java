package pl.rafalmag.subtitledownloader.title;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.moviejukebox.themoviedb.model.MovieDb;

public class MovieTest {

	@Test
	public void should_get_year() throws Exception {
		// given
		MovieDb movieDb = new MovieDb();
		movieDb.setReleaseDate("1977-05-25");
		int expectedYear = 1977;

		// when
		int year = Movie.getYear(movieDb);

		// then
		assertThat(year, equalTo(expectedYear));
	}

	@Test
	public void should_get_year_work_for_not_valid_release_date()
			throws Exception {
		// given
		MovieDb movieDb = new MovieDb();
		movieDb.setReleaseDate("invalid");
		int expectedYear = -1;

		// when
		int year = Movie.getYear(movieDb);

		// then
		assertThat(year, equalTo(expectedYear));
	}

	@Test
	public void should_get_year_work_for_null_release_date() throws Exception {
		// given
		MovieDb movieDb = new MovieDb();
		movieDb.setReleaseDate(null);
		int expectedYear = -1;

		// when
		int year = Movie.getYear(movieDb);

		// then
		assertThat(year, equalTo(expectedYear));
	}

}
