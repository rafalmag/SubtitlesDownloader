package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
		SortedSet<Subtitles> set = Sets.newTreeSet(Collections.reverseOrder());

		Callable<List<Subtitles>> callable = new NamedCallable<>(
				"-FromOpenSub", new Callable<List<Subtitles>>() {

					@Override
					public List<Subtitles> call()
							throws SubtitlesDownloaderException,
							InterruptedException {
						return getSubtitlesFromOpenSubtitles(timeoutMs);
					}
				});
		/*
		 * FIXME strange that it cannot be inline - some java 1.7 / eclipse
		 * 3.7 compiler bug...
		 */
		Collection<Callable<List<Subtitles>>> solvers = ImmutableList
				.of(callable);
		Collection<List<Subtitles>> solve = Utils.solve(EXECUTOR,
				solvers, timeoutMs, progressCallback);

		for (List<Subtitles> item : solve) {
			set.addAll(item);
		}
		return set;
	}

	protected List<Subtitles> getSubtitlesFromOpenSubtitles(long timeoutMs)
			throws SubtitlesDownloaderException, InterruptedException {
		Session session = new Session();
		session.login(); // mandatory
		List<SearchSubtitlesResult> subtitlesFromOpenSubtitles = new CheckMovieSubtitles(
				session, movieFile, movie).getSubtitles(timeoutMs);
		return Lists.transform(subtitlesFromOpenSubtitles,
				new Function<SearchSubtitlesResult, Subtitles>() {

					@Override
					public Subtitles apply(SearchSubtitlesResult input) {
						return new Subtitles(input);
					}
				});
	}
}
