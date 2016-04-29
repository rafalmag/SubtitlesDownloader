package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class SubtitlesService {

    @InjectLogger
    private Logger LOG;

    @Inject
    private Session session;

    @Inject
    private CheckMovieSubtitles checkMovieSubtitles;

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
}
