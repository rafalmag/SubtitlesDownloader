package pl.rafalmag.subtitledownloader.title;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.moviejukebox.themoviedb.model.MovieDb;

public class TitleUtils {

	private final File movieFile;

	public TitleUtils(File movieFile) {
		this.movieFile = movieFile;
	}

	public List<Movie> getTitles() {
		String baseName = FilenameUtils.getBaseName(movieFile.getName());
		List<MovieDb> searchMovie = TheMovieDbHelper.getInstance().searchMovie(
				baseName);
		return Lists.transform(searchMovie, new Function<MovieDb, Movie>() {

			@Override
			public Movie apply(MovieDb input) {
				Movie movie = new Movie();
				movie.setImdbId(input.getImdbID());

				movie.setTitle(input.getTitle());
				int year = getYear(input);
				movie.setYear(year);
				return movie;
			}

		});
	}

	// eq. 1977-05-25
	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

	protected static int getYear(MovieDb input) {
		String releaseDate = input.getReleaseDate();
		Matcher matcher = YEAR_PATTERN.matcher(releaseDate);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		return 0;
	}
}
