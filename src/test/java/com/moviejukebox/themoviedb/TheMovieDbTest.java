package com.moviejukebox.themoviedb;

import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class TheMovieDbTest {

    private static String TM_DB_API_KEY = TheMovieDbHelper.API_KEY;

    @Test
    public void should_get_info_for_starWars_movie() throws Exception {
        // given
        String title = "Star Wars New Hope";
        String language = "english";
        boolean allResults = true;

        // when
        TheMovieDbApi theMovieDb = new TheMovieDbApi(TM_DB_API_KEY);
        List<MovieDb> searchMovie = theMovieDb.searchMovie(title, 0, language,
                allResults, 0).getResults();

        // then
        MovieDb firstMovieDb = searchMovie.get(0);
        assertThat(firstMovieDb.getTitle(),
                startsWith("Star Wars"));
        assertThat(firstMovieDb.getOriginalTitle(),
                startsWith("Star Wars"));
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
        TheMovieDbApi theMovieDb = new TheMovieDbApi(TM_DB_API_KEY);
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
        TheMovieDbApi theMovieDb = new TheMovieDbApi(TM_DB_API_KEY);
        List<MovieDb> searchMovie = theMovieDb.searchMovie(title, 0, language,
                true, 0).getResults();
        MovieDb firstMovieDb = searchMovie.get(0);

        MovieDb movieDbById = theMovieDb.getMovieInfo(id, language);

        // then
        assertThat(movieDbById.getId(), equalTo(firstMovieDb.getId()));
        assertThat(movieDbById.getTitle(), equalTo(firstMovieDb.getTitle()));
        assertThat(movieDbById.getOriginalTitle(),
                equalTo(firstMovieDb.getOriginalTitle()));
        assertThat(movieDbById.getReleaseDate(),
                equalTo(firstMovieDb.getReleaseDate()));
        // does'n not equal !
        // assertThat(movieDbById, equalTo(firstMovieDb));

        // firstMovieDb doesn't have imdb
        // assertThat(movieDbById.getImdbID(),
        // equalTo(firstMovieDb.getImdbID()));
        // popularity differs - mystery !
//		assertThat(movieDbById.getPopularity(),
//				equalTo(firstMovieDb.getPopularity()));

		/*
         * movieDbById:
		 * [MovieDB=[backdropPath=/4iJfYYoQzZcONB9hNzg0J0wWyPH.jpg],[id=11],
		 * [originalTitle=Star Wars],[popularity=7.311884],
		 * [posterPath=/tvSlBzAdRE29bZe5yYWrJ2ds137.jpg],
		 * [releaseDate=1977-03-20],[title=Star Wars],[adult=false],
		 * [belongsToCollection=[Collection=[id=10],[title=null],
		 * [name=Star Wars Collection],[posterPath=/ghd5zOQnDaDW1mxO7R5fXXpZMu.jpg],
		 * [backdropPath=/d8duYyyC9J5T825Hg7grmaabfxQ.jpg],[releaseDate=null]]],
		 * [budget=11000000],[genres=[[Genre=id=12],[name=Adventure]],
		 * [Genre=id=28],[name=Action]], [Genre=id=878],
		 * [name=Science Fiction]]]],[homepage=http://www.starwars.com],
		 * [imdbID=tt0076759],
		 * [overview=Princess Leia is captured and held hostage by the evil
		 * Imperial forces in their effort to take over the galactic Empire.
		 * Venturesome Luke Skywalker and dashing captain Han Solo team together
		 * with the loveable robot duo R2-D2 and C-3PO to rescue the beautiful
		 * princess and restore peace and justice in the Empire.],
		 * [productionCompanies=[[ProductionCompany=[id=1],[name=Lucasfilm]],
		 * [ProductionCompany=[id=306],[name=Twentieth Century Fox Film Corporation]]]],
		 * [productionCountries=[[ProductionCountry=[isoCode=US],
		 * [name=United States of America]]]],[revenue=775398007],
		 * [runtime=121],[spokenLanguages=[[Language=isoCode=en, name=English]]]],
		 * [tagline=A long time ago in a galaxy far, far away...],
		 * [voteAverage=7.8],[voteCount=3554]]
		 * 
		 * firstMovieDb:
		 * 
		 * [MovieDB=[backdropPath=/4iJfYYoQzZcONB9hNzg0J0wWyPH.jpg],[id=11],
		 * [originalTitle=Star Wars],[popularity=8.311884],
		 * [posterPath=/tvSlBzAdRE29bZe5yYWrJ2ds137.jpg],
		 * [releaseDate=1977-03-20],[title=Star Wars],[adult=false],
		 * [belongsToCollection=null],[budget=0],[genres=null],
		 * [homepage=null],[imdbID=null],
		 * [overview=Princess Leia is captured and held hostage by the evil
		 * Imperial forces in their effort to take over the galactic Empire.
		 * Venturesome Luke Skywalker and dashing captain Han Solo team together
		 * with the loveable robot duo R2-D2 and C-3PO to rescue the beautiful
		 * princess and restore peace and justice in the Empire.],
		 * [productionCompanies=null],[productionCountries=null],
		 * [revenue=0],[runtime=0],[spokenLanguages=null],[tagline=null],
		 * [voteAverage=7.79],[voteCount=3554]]
		 */
    }
}
