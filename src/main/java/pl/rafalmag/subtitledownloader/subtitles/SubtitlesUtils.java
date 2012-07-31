package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.Utils;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SubtitlesUtils {

	private final Movie movie;
	private final File movieFile;

	public SubtitlesUtils(Movie movie, File movieFile) {
		this.movie = movie;
		this.movieFile = movieFile;
	}

	public SortedSet<Subtitles> getSubtitles(final long timeoutMs)
			throws InterruptedException {
		SortedSet<Subtitles> set = Sets.newTreeSet(Collections.reverseOrder());

		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
		try {
			Callable<List<Subtitles>> callable = new Callable<List<Subtitles>>() {

				@Override
				public List<Subtitles> call() throws Exception {
					return getSubtitlesFromOpenSubtitles(timeoutMs);
				}
			};
			/*
			 * FIXME strange that it cannot be inline - some java 1.7 / eclipse
			 * 3.7 compiler bug...
			 */
			Collection<Callable<List<Subtitles>>> solvers = ImmutableList
					.of(callable);
			Collection<List<Subtitles>> solve = Utils.solve(newFixedThreadPool,
					solvers, timeoutMs);

			for (List<Subtitles> item : solve) {
				set.addAll(item);
			}
			return set;
		} finally {
			newFixedThreadPool.shutdown();
		}
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
