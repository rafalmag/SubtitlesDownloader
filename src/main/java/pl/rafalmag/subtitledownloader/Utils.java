package pl.rafalmag.subtitledownloader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static <T> Collection<T> solve(Executor e,
			Collection<Callable<T>> solvers, long timeoutMs)
			throws InterruptedException {
		List<T> results = Lists.newLinkedList();

		CompletionService<T> ecs = new ExecutorCompletionService<T>(e);
		for (Callable<T> s : solvers)
			ecs.submit(s);
		int n = solvers.size();

		long stopTime = timeoutMs + System.currentTimeMillis();

		for (int i = 0; i < n; ++i) {
			try {
				T r = ecs.take().get();
				results.add(r);
			} catch (ExecutionException e1) {
				Throwable cause = e1.getCause();
				LOGGER.error("Exception in task: " + cause.getMessage(), cause);
			}
			if (stopTime < System.currentTimeMillis()) {
				LOGGER.warn("Timeout occurred");
				break;
			}
		}
		return results;
	}
}
