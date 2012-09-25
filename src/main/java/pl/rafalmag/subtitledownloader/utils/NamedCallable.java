package pl.rafalmag.subtitledownloader.utils;

import java.util.concurrent.Callable;

public class NamedCallable<V> implements Callable<V> {

	private final String name;
	private final Callable<V> callable;

	public NamedCallable(String name, Callable<V> callable) {
		this.name = name;
		this.callable = callable;
	}

	@Override
	public V call() throws Exception {
		final String orgName = Thread.currentThread().getName();
		Thread.currentThread().setName(orgName + name);
		try {
			return callable.call();
		} finally {
			Thread.currentThread().setName(orgName);
		}

	}

}
