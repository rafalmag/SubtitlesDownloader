package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.DeflaterOutputStream;

@Singleton
public class SubtitlesService {

    @InjectLogger
    private Logger LOG;

    @Inject
    private Session session;

    @Inject
    private CheckMovieSubtitles checkMovieSubtitles;

    @Inject
    private CheckMovie checkMovie;

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    private final static ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
                    .namingPattern("Subtitle-%d").build());

    public SortedSet<Subtitles> getSubtitles(Movie movie, File movieFile, long timeoutMs,
                                             ProgressCallback progressCallback)
            throws InterruptedException {
        LOG.debug("search subtitles for {} with timeout {}ms", movie, timeoutMs);
        Callable<List<Subtitles>> callable = new NamedCallable<>(
                "-FromOpenSub", () -> getSubtitlesFromOpenSubtitles(movie, movieFile, timeoutMs));
        Collection<List<Subtitles>> solve = Utils.solve(EXECUTOR,
                ImmutableList.of(callable), timeoutMs, progressCallback);

        Supplier<TreeSet<Subtitles>> supplier = () -> new TreeSet<>(Collections.reverseOrder());
        return StreamSupport.stream(solve.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .collect(Collectors.toCollection(supplier));
    }

    protected List<Subtitles> getSubtitlesFromOpenSubtitles(Movie movie, File movieFile, long timeoutMs)
            throws SubtitlesDownloaderException, InterruptedException {
        List<SearchSubtitlesResult> subtitlesFromOpenSubtitles = checkMovieSubtitles
                .getSubtitles(movie, movieFile, timeoutMs);
        return subtitlesFromOpenSubtitles.stream().map(Subtitles::new).collect(Collectors.toList());
    }

    public void uploadSubtitles(Movie movie, File movieFile, File subtitles) throws SubtitlesDownloaderException {
        String movieFileName = movieFile.getName();
        String subtitleMd5Hash = md5(subtitles);
        String movieHash = checkMovie.getHashCode(movieFile);
        long movieSizeByte = checkMovie.getByteSize(movieFile);
        String subtitleFileName = subtitles.getName();
        boolean alreadyInDb = session.tryUploadSubtitles(
                subtitleMd5Hash,
                subtitleFileName,
                movieHash,
                movieSizeByte,
                movieFileName);
        if (!alreadyInDb) {
            // TODO DetectLanguage to verify if their lang matches subtitles language from application
            String idMovieImdb = Integer.toString(movie.getImdbId());
            String movieReleaseName = FilenameUtils.getBaseName(movieFileName);
            String subtitleLanguageId = subtitlesDownloaderProperties.getSubtitlesLanguage().getId();
            String subtitleContent = gzip(subtitles);
            session.uploadSubtitles(
                    idMovieImdb,
                    movieReleaseName,
                    subtitleLanguageId,
                    subtitleMd5Hash,
                    subtitleFileName,
                    movieHash,
                    movieSizeByte,
                    movieFileName,
                    subtitleContent);
        }
    }

    @VisibleForTesting
    String md5(File file) throws SubtitlesDownloaderException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        } catch (IOException e) {
            throw new SubtitlesDownloaderException("Could not calculate md5 for " + file + ", because of " + e.getMessage(), e);
        }
    }

    @VisibleForTesting
    String gzip(File file) throws SubtitlesDownloaderException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream is = new FileInputStream(file);
             Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream, true, -1, null);
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(base64OutputStream)
        ) {
            ByteStreams.copy(is, deflaterOutputStream);
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new SubtitlesDownloaderException("Could not gzip " + file + ", because of " + e.getMessage(), e);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return new String(bytes, Charsets.UTF_8);
    }

}
