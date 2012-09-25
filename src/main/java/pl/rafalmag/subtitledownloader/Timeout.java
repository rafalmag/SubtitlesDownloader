package pl.rafalmag.subtitledownloader;

import java.util.concurrent.TimeUnit;

public class Timeout {

	private final long startTimeMs;
	private final long stopTimeMs;
	private final long timeoutMs;

	public Timeout(long timeout, TimeUnit timeUnit) {
		startTimeMs = System.currentTimeMillis();
		timeoutMs = timeUnit.toMillis(timeout);
		stopTimeMs = startTimeMs + timeoutMs;
	}

	/**
	 * 
	 * @param timeUnit
	 * @return elapsed time or initial timeout if reached
	 */
	public long getElapsedTime(TimeUnit timeUnit) {
		long elapsedMs = System.currentTimeMillis() - startTimeMs;
		elapsedMs = Math.min(elapsedMs, timeoutMs);
		return timeUnit.convert(elapsedMs, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @param timeUnit
	 * @return time left or 0 if reached
	 */
	public long getTimeLeft(TimeUnit timeUnit) {
		long timeLeftMs = stopTimeMs - System.currentTimeMillis();
		if (timeLeftMs < 0) {
			timeLeftMs = 0;
		}
		return timeUnit.convert(timeLeftMs, TimeUnit.MILLISECONDS);
	}

	public long getTimeLeftAtMost(long maxTime, TimeUnit timeUnit) {
		long timeLeft = getTimeLeft(timeUnit);
		return Math.min(timeLeft, maxTime);
	}

	public boolean isReached() {
		long timeLeft = getTimeLeft(TimeUnit.MILLISECONDS);
		return timeLeft == 0;
	}

}
