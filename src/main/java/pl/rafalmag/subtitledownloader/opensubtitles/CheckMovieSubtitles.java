package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.Utils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CheckMovieSubtitles extends CheckMovie {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CheckMovieSubtitles.class);

    private final Movie movie;

    public CheckMovieSubtitles(Session session, File movieFile, Movie movie) {
        super(session, movieFile);
        this.movie = movie;
    }

    protected List<SearchSubtitlesResult> getSubtitlesByImdb()
            throws SubtitlesDownloaderException {
        return session.searchSubtitlesBy(movie.getImdbId());
    }

    protected List<SearchSubtitlesResult> getSubtitlesByTitle()
            throws SubtitlesDownloaderException {
        String title = movie.getTitle();
        return session.searchSubtitlesBy(title);
    }

    private final static ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
                    .namingPattern("OpenSubtitle-%d").build());

    public List<SearchSubtitlesResult> getSubtitles(long timeoutMs)
            throws InterruptedException {
        LOGGER.debug("search openSubtitles for {} with timeout {}ms", movie,
                timeoutMs);
        Collection<? extends Callable<List<SearchSubtitlesResult>>> solvers = ImmutableList
                .of(
                        new NamedCallable<>("-ByTitle", this::getSubtitlesByTitle),
                        new NamedCallable<>("-ByImdb", this::getSubtitlesByImdb),
                        new NamedCallable<>("-ByMovieHashAndByteSize", this::getSubtitlesByMovieHashAndByteSize)
                );
        Collection<List<SearchSubtitlesResult>> solve = Utils.solve(EXECUTOR, solvers, timeoutMs);

        return StreamSupport.stream(solve.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .filter(i -> i.getIDMovieImdb() == movie.getImdbId())
                .collect(Collectors.toList());
    }
}
