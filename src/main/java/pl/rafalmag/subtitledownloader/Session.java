package pl.rafalmag.subtitledownloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class Session {

	private static final String USER_AGENT = "OS Test User Agent"; // TODO
																	// change
																	// this

	private static final Logger LOGGER = Logger.getLogger(Session.class);

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
		login("", "", "en", USER_AGENT);
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
			return parseImdbMovieDetails(data);
		} catch (XmlRpcException e) {
			throw new SubtitlesDownloaderException(
					"could not invoke GetIMDBMovieDetails because of "
							+ e.getMessage(), e);
		}
	}

	private ImdbMovieDetails parseImdbMovieDetails(Map<String, Object> data) {
		ImdbMovieDetails imdbMovieDetails = new ImdbMovieDetails();
		// imdbMovieDetails.setCast(data.get("cast")); // TODO
		// imdbMovieDetails.setAka(data.get("aka")); // TODO
		imdbMovieDetails.setRating(Double.parseDouble((String) data
				.get("rating")));
		imdbMovieDetails.setCoverUrl((String) data.get("cover"));
		imdbMovieDetails.setId(Integer.parseInt((String) data.get("id")));
		imdbMovieDetails.setVotes(Integer.parseInt((String) data.get("votes")));
		imdbMovieDetails.setTitle((String) data.get("title"));
		imdbMovieDetails.setYear(Integer.parseInt((String) data.get("year")));
		LOGGER.debug("parsed imdbMovieDetails=" + imdbMovieDetails);
		return imdbMovieDetails;
	}

	public Collection<CheckMovieHash2Entity> checkMovieHash2(String hashCode)
			throws SubtitlesDownloaderException {
		Object[] params = new Object[] { token, new Object[] { hashCode } };
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> execute = (Map<String, Object>) client.execute(
					"CheckMovieHash2", params);
			LOGGER.debug("CheckMovieHash2 response: " + execute);
			String status = (String) execute.get("status");
			if (!status.contains("OK")) {
				throw new SubtitlesDownloaderException(
						"could not CheckMovieHash2 because of wrong status "
								+ status);
			}
			Object[] object = (Object[]) execute.get("data");
			Collection<CheckMovieHash2Entity> result = Lists
					.newArrayListWithCapacity(object.length);
			for (Object record : object) {
				CheckMovieHash2Entity checkMovieHash2Entity = parseCheckMovieHash2Entity((Map<String, Object>) record);
				result.add(checkMovieHash2Entity);
			}
			return result;
		} catch (XmlRpcException e) {
			throw new SubtitlesDownloaderException(
					"could not invoke CheckMovieHash2 because of "
							+ e.getMessage(), e);
		}
	}

	private CheckMovieHash2Entity parseCheckMovieHash2Entity(
			Map<String, Object> record) {
		String movieHash = (String) record.get("MovieHash");
		int imdbId = (Integer) record.get("MovieImdbID");
		String movieName = (String) record.get("MovieName");
		int year = (Integer) record.get("MovieYear");
		int seenCount = (Integer) record.get("SeenCount");
		CheckMovieHash2Entity checkMovieHash2Entity = new CheckMovieHash2Entity(
				movieHash, imdbId, movieName, year, seenCount);
		LOGGER.debug("parsed checkMovieHash2Entity=" + checkMovieHash2Entity);
		return checkMovieHash2Entity;
	}

}
