package com.moviejukebox.themoviedb;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import com.moviejukebox.themoviedb.model.MovieDb;

public class TheMovieDbTest {

	private static String TM_DB_API_KEY = TheMovieDbHelper.API_KEY;

	@Test
	public void should_get_imdb_id_for_starWars_movie() throws Exception {
		// given
		String title = "Star Wars";
		String language = "english";
		boolean allResults = true;

		// when
		TheMovieDb theMovieDb = new TheMovieDb(TM_DB_API_KEY);
		List<MovieDb> searchMovie = theMovieDb.searchMovie(title, language,
				allResults);

		MovieDb firstMovieDb = searchMovie.get(0);
		assertThat(firstMovieDb.getTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getOriginalTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getId(), equalTo(11));
	}
}
