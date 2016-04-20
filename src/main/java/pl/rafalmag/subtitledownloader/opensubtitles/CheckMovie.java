package pl.rafalmag.subtitledownloader.opensubtitles;

import org.opensubtitles.OpenSubtitlesHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CheckMovie {

    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);

    protected final Session session;

    protected final File movieFile;

    private String hashCode;

    public CheckMovie(Session session, File movieFile) {
        this.session = session;
        this.movieFile = movieFile;
    }

    public List<MovieEntity> getTitleInfo()
            throws SubtitlesDownloaderException {
        String hashCode = getHashCode();
        return session.checkMovieHash2(hashCode);
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

    protected List<SearchSubtitlesResult> getSubtitlesByMovieHashAndByteSize()
            throws SubtitlesDownloaderException {

        String movieHash = getHashCode();
        long movieByteSize = getByteSize();
        return session.searchSubtitlesBy(movieHash, movieByteSize);
    }

    public long getByteSize() {
        return movieFile.length();
    }

}
