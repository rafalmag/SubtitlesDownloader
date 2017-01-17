package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.opensubtitles.OpenSubtitlesHasher;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.MovieEntity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class CheckMovie {

    @InjectLogger
    protected Logger LOG;

    @Inject
    protected Session session;

    private final Cache<File, String> fileHashes = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public List<MovieEntity> getTitleInfo(File movieFile) throws SubtitlesDownloaderException {
        String hashCode = getHashCode(movieFile);
        return session.checkMovieHash2(hashCode);
    }

    public String getHashCode(File movieFile) throws SubtitlesDownloaderException {
        try {
            String hashCode = fileHashes.get(movieFile, () -> OpenSubtitlesHasher.computeHash(movieFile));
            LOG.debug("hashCode={}", hashCode);
            return hashCode;
        } catch (ExecutionException e) {
            throw new SubtitlesDownloaderException("Could not get hashcode for " + movieFile.getName()
                    + ", because of " + e.getCause().getMessage(), e.getCause());
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
