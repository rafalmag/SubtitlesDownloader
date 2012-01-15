package pl.rafalmag.subtitledownloader.opensubtitles;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.opensubtitles.OpenSubtitlesHasher;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

// TODO check http://api.themoviedb.org
public class CheckMovie {

	private static final Logger LOGGER = Logger.getLogger(Session.class);

	private final Session session;

	private final File movieFile;

	private String hashCode;

	public CheckMovie(Session session, File movieFile) {
		this.session = session;
		this.movieFile = movieFile;
	}

	@Deprecated
	public Collection<CheckMovieHash2Entity> getTitleInfo()
			throws SubtitlesDownloaderException {
		String hashCode = getHashCode();
		Collection<CheckMovieHash2Entity> checkMovieHash2Map = session
				.checkMovieHash2(hashCode);
		return checkMovieHash2Map;
	}

	public String getHashCode() throws SubtitlesDownloaderException {
		if (hashCode == null) {
			try {
				hashCode = OpenSubtitlesHasher.computeHash(movieFile);
				LOGGER.debug("hashCode=" + hashCode);
			} catch (IOException e) {
				throw new SubtitlesDownloaderException(
						"Could not get hashcode for " + movieFile
								+ " because of " + e.getMessage(), e);
			}
		}
		return hashCode;
	}

	public Collection<SearchSubtitlesResult> getSubtitles()
			throws SubtitlesDownloaderException {

		String movieHash = getHashCode();
		long movieByteSize = getByteSize();
		return session.searchSubtitles(movieHash, movieByteSize);
	}

	public long getByteSize() {
		return movieFile.length();
	}

}
