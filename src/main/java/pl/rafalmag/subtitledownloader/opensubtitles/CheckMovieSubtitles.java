package pl.rafalmag.subtitledownloader.opensubtitles;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class CheckMovieSubtitles extends CheckMovie {

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
		Collection<? extends Callable<List<SearchSubtitlesResult>>> solvers = ImmutableList
				.of(
						new NamedCallable<>(
								"-ByTitle",
								new Callable<List<SearchSubtitlesResult>>() {

									@Override
									public List<SearchSubtitlesResult> call()
											throws SubtitlesDownloaderException {
										return getSubtitlesByTitle();
									}
								}),
						new NamedCallable<>(
								"-ByImdb",
								new Callable<List<SearchSubtitlesResult>>() {

									@Override
									public List<SearchSubtitlesResult> call()
											throws SubtitlesDownloaderException {
										return getSubtitlesByImdb();
									}
								}),
						new NamedCallable<>(
								"-ByMovieHashAndByteSize",
								new Callable<List<SearchSubtitlesResult>>() {

									@Override
									public List<SearchSubtitlesResult> call()
											throws SubtitlesDownloaderException {
										return getSubtitlesByMovieHashAndByteSize();
									}
								})
				);
		Collection<List<SearchSubtitlesResult>> solve = Utils.solve(
				EXECUTOR, solvers, timeoutMs);
		// TODO maybe other custom collection :
		// Multimap<SearchSubtitlesResult, SearchMethod>
		Set<SearchSubtitlesResult> set = Sets.newHashSet();
		for (List<SearchSubtitlesResult> item : solve) {
			set.addAll(item);
		}

		List<SearchSubtitlesResult> validImdbSubtitles = select(
				set,
				having(on(SearchSubtitlesResult.class).getIDMovieImdb(),
						equalTo(movie.getImdbId())));
		return validImdbSubtitles;
	}
}
