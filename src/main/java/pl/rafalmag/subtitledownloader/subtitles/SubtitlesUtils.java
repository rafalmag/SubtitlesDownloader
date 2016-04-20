package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SubtitlesUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SubtitlesUtils.class);

	private final Movie movie;
	private final File movieFile;
	private final long timeoutMs;
	private final ProgressCallback progressCallback;

	public SubtitlesUtils(Movie movie, File movieFile, long timeoutMs,
			ProgressCallback progressCallback) {
		this.movie = movie;
		this.movieFile = movieFile;
		this.timeoutMs = timeoutMs;
		this.progressCallback = progressCallback;
	}

	private final static ExecutorService EXECUTOR = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
					.namingPattern("Subtitle-%d").build());

	public SortedSet<Subtitles> getSubtitles()
			throws InterruptedException {
		LOGGER.debug("search subtitles for {} with timeout {}ms", movie,
				timeoutMs);
		Callable<List<Subtitles>> callable = new NamedCallable<>(
				"-FromOpenSub", () -> getSubtitlesFromOpenSubtitles(timeoutMs));
		Collection<List<Subtitles>> solve = Utils.solve(EXECUTOR,
				ImmutableList
						.of(callable), timeoutMs, progressCallback);

		Supplier<TreeSet<Subtitles>> supplier = () -> new TreeSet<>(Collections.reverseOrder());
		return StreamSupport.stream(solve.spliterator(), false).flatMap(i -> StreamSupport.stream(i.spliterator(), false)).collect(Collectors.toCollection(supplier));
	}

	protected List<Subtitles> getSubtitlesFromOpenSubtitles(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		Session session = new Session();
		session.login(); // mandatory
		List<SearchSubtitlesResult> subtitlesFromOpenSubtitles = new CheckMovieSubtitles(
				session, movieFile, movie).getSubtitles(timeoutMs);
		return subtitlesFromOpenSubtitles.stream().map(Subtitles::new).collect(Collectors.toList());
	}
}
