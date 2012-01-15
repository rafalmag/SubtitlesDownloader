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
			Object object = execute.get("data");
			// Collection<CheckMovieHash2Entity> result =
			// Lists.newArrayListWithCapacity(object.size());
			// for(Map<String, Object> record : object){
			//
			// CheckMovieHash2Entity checkMovieHash2Entity =
			// parseCheckMovieHash2Entity(record);
			// }
			// return checkMovieHash2Entity;
			return null;
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
		return new CheckMovieHash2Entity(movieHash, imdbId, movieName, year,
				seenCount);
	}

}
