package pl.rafalmag.subtitledownloader.opensubtitles;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.title.TitleUtils;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.Utils;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CheckMovieSubtitles extends CheckMovie {

    @InjectLogger
    private Logger LOG;

    @Inject
    private TitleUtils titleUtils;

    protected List<SearchSubtitlesResult> getSubtitlesByImdb(Movie movie)
            throws SubtitlesDownloaderException {
        return session.searchSubtitlesBy(movie.getImdbId());
    }

    protected List<SearchSubtitlesResult> getSubtitlesByTitle(Movie movie)
            throws SubtitlesDownloaderException {
        String title = movie.getTitle();
        return session.searchSubtitlesBy(title);
    }

    private final static ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
                    .namingPattern("OpenSubtitle-%d").build());

    public List<SearchSubtitlesResult> getSubtitles(Movie movie, File movieFile, long timeoutMs)
            throws InterruptedException {
        LOG.debug("search openSubtitles for {} with timeout {}ms", movie,
                timeoutMs);
        Collection<? extends Callable<List<SearchSubtitlesResult>>> solvers = ImmutableList
                .of(
                        new NamedCallable<>("-ByTitle", () -> getSubtitlesByTitle(movie)),
                        new NamedCallable<>("-ByImdb", () -> getSubtitlesByImdb(movie)),
                        new NamedCallable<>("-ByMovieHashAndByteSize", () -> getSubtitlesByMovieHashAndByteSize(movieFile))
                );
        Collection<List<SearchSubtitlesResult>> solve = Utils.solve(EXECUTOR, solvers, timeoutMs);

        return StreamSupport.stream(solve.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .filter(i -> i.getIDMovieImdb() == movie.getImdbId())
                .collect(Collectors.toList());
    }
}
