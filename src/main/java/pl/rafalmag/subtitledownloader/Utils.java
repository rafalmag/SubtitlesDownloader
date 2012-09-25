package pl.rafalmag.subtitledownloader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	// TODO check calls
	public static <T> Collection<T> solve(Executor executor,
			Collection<? extends Callable<T>> solvers, long timeoutMs)
			throws InterruptedException {
		List<T> results = Lists.newLinkedList();

		CompletionService<T> ecs = new ExecutorCompletionService<T>(executor);
		List<Future<T>> futuresList = Lists.newArrayList();
		for (Callable<T> s : solvers) {
			Future<T> future = ecs.submit(s);
			futuresList.add(future);
		}
		int solversSize = solvers.size();

		long stopTime = timeoutMs + System.currentTimeMillis();

		for (int i = 0; i < solversSize; ++i) {
			try {
				T r = ecs.take().get();
				results.add(r);
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				LOGGER.error("Exception in task: " + cause.getMessage(), cause);
			}
			if (stopTime < System.currentTimeMillis()) {
				LOGGER.warn("Timeout " + timeoutMs + "ms occurred");
				for (Future<T> future : futuresList) {
					future.cancel(true);
				}
				break;
			}
		}
		return results;
	}
}
