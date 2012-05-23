package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.io.FilenameUtils;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtils {

	private final File movieFile;

	public TitleUtils(File movieFile) {
		this.movieFile = movieFile;
	}

	public SortedSet<Movie> getTitles() throws SubtitlesDownloaderException {
		String baseName = FilenameUtils.getBaseName(movieFile.getName());
		SortedSet<Movie> set = Sets.newTreeSet(new Comparator<Movie>() {

			@Override
			public int compare(Movie o1, Movie o2) {
				// TODO move it, enhance it
				return o1.getTitle().compareTo(o2.getTitle());
			}

		});
		set.addAll(getByTitle(baseName));
		set.addAll(getByFileHash());
		return set;
	}

	protected List<Movie> getByTitle(String title) {
		List<MovieDb> searchMovie = TheMovieDbHelper.getInstance().searchMovie(
				title);
		List<Movie> list = Lists.transform(searchMovie,
				new Function<MovieDb, Movie>() {

					@Override
					public Movie apply(MovieDb input) {
						return new Movie(input);
					}

				});
		return list;
	}

	public List<Movie> getByFileHash() throws SubtitlesDownloaderException {
		Session session = new Session();
		session.login();
		CheckMovie checkMovie = new CheckMovie(session, movieFile);

		// when
		List<CheckMovieHash2Entity> checkMovieHash2Entities = checkMovie
				.getTitleInfo();

		List<Movie> list = Lists.transform(checkMovieHash2Entities,
				new Function<CheckMovieHash2Entity, Movie>() {

					@Override
					public Movie apply(CheckMovieHash2Entity input) {
						return new Movie(input);
					}

				});
		return list;
	}

}
