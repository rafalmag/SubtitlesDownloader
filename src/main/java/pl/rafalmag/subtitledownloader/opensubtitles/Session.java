package pl.rafalmag.subtitledownloader.opensubtitles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.CheckMovieHash2Entity;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.ImdbMovieDetails;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class Session {

	private static final String ENG = "eng";

	private static final String USER_AGENT = "SubtitlesDownloader v1";

	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);

	private final XmlRpcClient client;
	private String token;

	public Session() {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("http://api.opensubtitles.org/xml-rpc"));
		} catch (MalformedURLException e) {
			Throwables.propagate(e);
		}
		client = new XmlRpcClient();
		client.setConfig(config);

	}

	public void login() throws SessionException {
		login("", "", "en", USER_AGENT); // TODO provide user and pass
	}

	public void login(String userName, String password, String language,
			String userAgent) throws SessionException {
		Object[] params = new Object[] { userName, password, language,
				userAgent };
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> execute = (Map<String, Object>) client.execute(
					"LogIn", params);
			LOGGER.debug("login response: " + execute);
			String status = (String) execute.get("status");
			if (!status.contains("OK")) {
				throw new SessionException(
						"could not login because of wrong status " + status);
			}
			token = (String) execute.get("token");
		} catch (XmlRpcException e) {
			throw new SessionException("could not login because of "
					+ e.getMessage(), e);
		}
	}

	public boolean logout() {
		Object[] params = new Object[] { token };
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> execute = (Map<String, Object>) client.execute(
					"LogOut", params);
			LOGGER.debug("logout response: " + execute);
			String status = (String) execute.get("status");
			if (!status.contains("OK")) {
				LOGGER.error("could not logout because of wrong status "
						+ status);
				return false;
			}
			token = null;
			return true;
		} catch (XmlRpcException e) {
			LOGGER.error("could not logout because of " + e.getMessage(), e);
			return false;
		}
	}

	public List<SearchSubtitlesResult> searchSubtitlesBy(String movieHash,
			Long movieByteSize) throws SubtitlesDownloaderException {
		Object[] params = new Object[] {
				token,
				new Object[] { ImmutableMap.of("sublanguageid", ENG,
						"moviehash", movieHash, "moviebytesize",
						movieByteSize.toString()) } };
		return searchSubtitles(params);
	}

	public List<SearchSubtitlesResult> searchSubtitlesBy(int imdbId)
			throws SubtitlesDownloaderException {
		Object[] params = new Object[] {
				token,
				new Object[] { ImmutableMap.of("sublanguageid", ENG, "imdbid",
						imdbId) } };
		return searchSubtitles(params);
	}

	public List<SearchSubtitlesResult> searchSubtitlesBy(String title)
			throws SubtitlesDownloaderException {
		Object[] params = new Object[] {
				token,
				new Object[] { ImmutableMap.of("sublanguageid", ENG, "query",
						title) } };
		return searchSubtitles(params);
	}

	private List<SearchSubtitlesResult> searchSubtitles(Object[] params)
			throws SubtitlesDownloaderException {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> execute = (Map<String, Object>) client.execute(
					"SearchSubtitles", params);
			LOGGER.debug("SearchSubtitles response: " + execute);
			String status = (String) execute.get("status");
			if (!status.contains("OK")) {
				throw new SubtitlesDownloaderException(
						"could not SearchSubtitles because of wrong status "
								+ status);
			}

			Object dataRaw = execute.get("data");
			// can be false if nothing found...
			if (dataRaw.equals(false)) {
				return Collections.emptyList();
			}
			Object[] data = (Object[]) dataRaw;
			List<SearchSubtitlesResult> result = Lists
					.newArrayListWithCapacity(data.length);
			for (Object entry : data) {
				@SuppressWarnings("unchecked")
				Map<String, Object> entryMap = (Map<String, Object>) entry;
				result.add(new SearchSubtitlesResult(entryMap));
			}
			return result;
		} catch (XmlRpcException e) {
			throw new SubtitlesDownloaderException(
					"could not invoke SearchSubtitles because of "
							+ e.getMessage(), e);
		}
	}

	// TODO
	// this can be also easily implemented
	// http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#SearchMoviesOnIMDB

	/**
	 * @deprecated Use TheMovieDb methods
	 * 
	 * @see {@link link
	 *      http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC
	 *      #GetIMDBMovieDetails}
	 * @param imdbId
	 * @return
	 * @throws SubtitlesDownloaderException
	 */
	@Deprecated
	public ImdbMovieDetails getImdbMovieDetails(int imdbId)
			throws SubtitlesDownloaderException {
		Object[] params = new Object[] { token, imdbId };
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> execute = (Map<String, Object>) client.execute(
					"GetIMDBMovieDetails", params);
			LOGGER.debug("GetIMDBMovieDetails response: " + execute);
			String status = (String) execute.get("status");
			if (!status.contains("OK")) {
				throw new SubtitlesDownloaderException(
						"could not GetIMDBMovieDetails because of wrong status "
								+ status);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) execute
					.get("data");
			return new ImdbMovieDetails(data);
		} catch (XmlRpcException e) {
			throw new SubtitlesDownloaderException(
					"could not invoke GetIMDBMovieDetails because of "
							+ e.getMessage(), e);
		}
	}

	public List<CheckMovieHash2Entity> checkMovieHash2(String hashCode)
			throws SubtitlesDownloaderException {
		Object[] params = new Object[] { token, new Object[] { hashCode } };
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> response = (Map<String, Object>) client
					.execute("CheckMovieHash2", params);
			LOGGER.debug("CheckMovieHash2 response: " + response);
			String status = (String) response.get("status");
			if (!status.contains("OK")) {
				throw new SubtitlesDownloaderException(
						"could not CheckMovieHash2 because of wrong status "
								+ status);
			}
			List<CheckMovieHash2Entity> result = parseData(response);
			return result;
		} catch (XmlRpcException e) {
			throw new SubtitlesDownloaderException(
					"could not invoke CheckMovieHash2 because of "
							+ e.getMessage(), e);
		}
	}

	private List<CheckMovieHash2Entity> parseData(Map<String, Object> response) {
		List<CheckMovieHash2Entity> result;
		Object data = response.get("data");
		if (data instanceof Object[]) {
			Object[] records = (Object[]) data;
			result = getRecords(records);
		} else if (data instanceof HashMap<?, ?>) {
			result = Lists.newArrayList();
			@SuppressWarnings("unchecked")
			Map<String, Object> recordsMap = (Map<String, Object>) data;
			for (Object records : recordsMap.values()) {
				result.addAll(getRecords((Object[]) records));
			}
		} else {
			throw new IllegalStateException("Unsupported data object type "
					+ data);
		}
		return result;
	}

	private List<CheckMovieHash2Entity> getRecords(Object[] records) {
		List<CheckMovieHash2Entity> result;
		result = Lists.newArrayListWithCapacity(records.length);
		for (Object record : records) {
			@SuppressWarnings("unchecked")
			CheckMovieHash2Entity checkMovieHash2Entity = new CheckMovieHash2Entity(
					(Map<String, Object>) record);
			result.add(checkMovieHash2Entity);
		}
		return result;
	}

}
