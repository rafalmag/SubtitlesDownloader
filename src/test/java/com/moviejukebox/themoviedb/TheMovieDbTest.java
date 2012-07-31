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
	public void should_get_info_for_starWars_movie() throws Exception {
		// given
		String title = "Star Wars New Hope";
		String language = "english";
		boolean allResults = true;

		// when
		TheMovieDb theMovieDb = new TheMovieDb(TM_DB_API_KEY);
		List<MovieDb> searchMovie = theMovieDb.searchMovie(title, language,
				allResults);

		// then
		MovieDb firstMovieDb = searchMovie.get(0);
		assertThat(firstMovieDb.getTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getOriginalTitle(),
				equalTo("Star Wars: Episode IV - A New Hope"));
		assertThat(firstMovieDb.getId(), equalTo(11));
		// assertThat(firstMovieDb.getImdbID(), equalTo("tt0076759")); // null
		// :(
	}

	@Test
	public void should_get_imdb_id_for_id() throws Exception {
		// given
		int id = 11;
		String expectedImdb = "tt0076759";
		String language = "english";

		// when
		TheMovieDb theMovieDb = new TheMovieDb(TM_DB_API_KEY);
		MovieDb movieInfo = theMovieDb.getMovieInfo(id, language);

		// then
		assertThat(movieInfo.getImdbID(), equalTo(expectedImdb));
	}

	@Test
	public void should_get_the_same_obj_by_title_and_id() throws Exception {
		// given
		String title = "Star Wars New Hope";
		String language = "english";
		int id = 11;

		// when
		TheMovieDb theMovieDb = new TheMovieDb(TM_DB_API_KEY);
		List<MovieDb> searchMovie = theMovieDb.searchMovie(title, language,
				true);
		MovieDb firstMovieDb = searchMovie.get(0);

		MovieDb movieDbById = theMovieDb.getMovieInfo(id, language);

		// then
		assertThat(movieDbById.getId(), equalTo(firstMovieDb.getId()));
		assertThat(movieDbById.getTitle(), equalTo(firstMovieDb.getTitle()));
		assertThat(movieDbById.getOriginalTitle(),
				equalTo(firstMovieDb.getOriginalTitle()));
		assertThat(movieDbById.getPopularity(),
				equalTo(firstMovieDb.getPopularity()));
		assertThat(movieDbById.getReleaseDate(),
				equalTo(firstMovieDb.getReleaseDate()));
		// does'n not equal !
		// assertThat(movieDbById, equalTo(firstMovieDb));

		// firstMovieDb doesn't have imdb
		// assertThat(movieDbById.getImdbID(),
		// equalTo(firstMovieDb.getImdbID()));

		/*
		 * movieDbById:
		 * <[MovieDB=[backdropPath=/r0v9dayXd1IH5WPWFBWv52tGHkB.jpg]
		 * ,[id=11],[originalTitle=Star Wars: Episode IV - A New
		 * Hope],[popularity
		 * =14188.862],[posterPath=/tvSlBzAdRE29bZe5yYWrJ2ds137.
		 * jpg],[releaseDate=1977-05-25],[title=Star Wars: Episode IV - A New
		 * Hope],[adult=false], [belongsToCollection=null],
		 * [budget=0],[genres=null
		 * ],[homepage=null],[imdbID=null],[overview=null]
		 * ,[productionCompanies=null
		 * ],[productionCountries=null],[revenue=0],[runtime
		 * =0],[spokenLanguages=
		 * null],[tagline=null],[voteAverage=9.1],[voteCount=122]]>
		 * 
		 * firstMovieDb:
		 * 
		 * <[MovieDB=[backdropPath=/r0v9dayXd1IH5WPWFBWv52tGHkB.jpg],[id=11],[
		 * originalTitle=Star Wars: Episode IV - A New
		 * Hope],[popularity=14188.862
		 * ],[posterPath=/tvSlBzAdRE29bZe5yYWrJ2ds137.
		 * jpg],[releaseDate=1977-05-25],[title=Star Wars: Episode IV - A New
		 * Hope],[adult=false],
		 * [belongsToCollection=[Collection=[id=10],[title=null],[name=Star Wars
		 * Collection
		 * ],[posterPath=/fdmSovGcTO4qeYH4llwqDsYi5cB.jpg],[backdropPath
		 * =/rgjAb1oUCzJk1U2WhtQt7gGu84U.jpg],[releaseDate=null]]],
		 * [budget=11000000],[genres=[[Genre=id=28],[name=Action]],
		 * [Genre=id=12],[name=Adventure]], [Genre=id=14],[name=Fantasy]],
		 * [Genre=id=878],[name=Science Fiction]],
		 * [Genre=id=10751],[name=Family]
		 * ]]],[homepage=null],[imdbID=tt0076759],[
		 * overview=null],[productionCompanies
		 * =[[ProductionCompany=[id=1],[name=Lucasfilm
		 * ]]]],[productionCountries=[
		 * [ProductionCountry=[isoCode=TN],[name=Tunisia]],
		 * [ProductionCountry=[isoCode=US],[name=United States of
		 * America]]]],[revenue
		 * =775398007],[runtime=0],[spokenLanguages=[[Language=isoCode=en,
		 * name=English]]]],[tagline=null],[voteAverage=9.1],[voteCount=122]]>|
		 */
	}
}
