package pl.rafalmag.subtitledownloader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	private static final long MAX_DELAY_MS = 100;

	public static <T> Collection<T> solve(Executor executor,
			Collection<? extends Callable<T>> solvers, long timeoutMs)
			throws InterruptedException {
		return solve(executor, solvers, timeoutMs, new ProgressCallback() {

			private double procDone = 0;

			@Override
			public void updateProgress(double procDone) {
				procDone = Math.min(1.0, procDone);
				procDone = Math.max(0.0, procDone);
				this.procDone = procDone;
			}

			@Override
			public void updateProgress(long progress, long max) {
				updateProgress((double) progress / (double) max);
			}

			@Override
			public double getProgress() {
				return procDone;
			}
		});
	}

	// TODO check calls
	public static <T> Collection<T> solve(Executor executor,
			Collection<? extends Callable<T>> solvers, long timeoutMs,
			ProgressCallback progressCallback)
			throws InterruptedException {
		List<T> results = Lists.newLinkedList();

		CompletionService<T> ecs = new ExecutorCompletionService<T>(executor);
		List<Future<T>> futuresList = Lists.newArrayList();
		for (Callable<T> s : solvers) {
			Future<T> future = ecs.submit(s);
			futuresList.add(future);
		}
		int solversSize = solvers.size();

		Timeout timeout = new Timeout(timeoutMs, TimeUnit.MILLISECONDS);

		while (results.size() != solversSize) {
			progressCallback.updateProgress(
					timeout.getElapsedTime(TimeUnit.MILLISECONDS), timeoutMs);
			try {
				Future<T> future = ecs.poll(timeout.getTimeLeftAtMost(
						MAX_DELAY_MS, TimeUnit.MILLISECONDS),
						TimeUnit.MILLISECONDS);
				if (future != null) {
					T r = future.get();
					results.add(r);
				}
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				LOGGER.error("Exception in task: " + cause.getMessage(), cause);
			}
			if (timeout.isReached()) {
				LOGGER.warn("Timeout " + timeoutMs + "ms occurred");
				for (Future<T> future : futuresList) {
					future.cancel(true);
				}
				break;
			}
		}
		progressCallback.updateProgress(1);
		return results;
	}
}
