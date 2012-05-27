package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.List;

import pl.rafalmag.subtitledownloader.title.Movie;

public class SubtitlesUtils {

	private final Movie movie;
	private final File movieFile;

	public SubtitlesUtils(Movie movie, File movieFile) {
		this.movie = movie;
		this.movieFile = movieFile;
	}

	public List<Subtitles> getSubtitles() {
		return null;
	}

}
