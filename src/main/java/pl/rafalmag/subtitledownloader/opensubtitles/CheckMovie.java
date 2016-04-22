package pl.rafalmag.subtitledownloader.opensubtitles;

import org.opensubtitles.OpenSubtitlesHasher;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Singleton
public class CheckMovie {

    @InjectLogger
    protected Logger LOG;

    @Inject
    protected Session session;

    public List<MovieEntity> getTitleInfo(File movieFile) throws SubtitlesDownloaderException {
        String hashCode = getHashCode(movieFile);
        return session.checkMovieHash2(hashCode);
    }

    public String getHashCode(File movieFile) throws SubtitlesDownloaderException {
        try {
            //TODO cache it ?
            String hashCode = OpenSubtitlesHasher.computeHash(movieFile);
            LOG.debug("hashCode=" + hashCode);
            return hashCode;
        } catch (IOException e) {
            throw new SubtitlesDownloaderException(
                    "Could not get hashcode for " + movieFile + " because of " + e.getMessage(), e);
        }
    }

    protected List<SearchSubtitlesResult> getSubtitlesByMovieHashAndByteSize(File movieFile)
            throws SubtitlesDownloaderException {
        String movieHash = getHashCode(movieFile);
        long movieByteSize = getByteSize(movieFile);
        return session.searchSubtitlesBy(movieHash, movieByteSize);
    }

    public long getByteSize(File movieFile) {
        return movieFile.length();
    }

}
