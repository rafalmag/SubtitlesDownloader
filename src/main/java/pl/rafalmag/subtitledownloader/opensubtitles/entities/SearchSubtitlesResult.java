package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#
 * SearchSubtitles
 * 
 * @author rafalmag
 * 
 */
public class SearchSubtitlesResult {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchSubtitlesResult.class);

	private final Map<String, Object> map;

	public SearchSubtitlesResult(Map<String, Object> map) {
		this.map = map;
		LOGGER.debug("parsed SearchSubtitlesResult=" + this);
	}

	public String getIdMovie() {
		return (String) map.get("IDMovie");
	}

	public String getIDMovieImdb() {
		return (String) map.get("IDMovieImdb");
	}

	public String getTitle() {
		return (String) map.get("MovieName");
	}

	public String getSubDownloadsCnt() {
		return (String) map.get("SubDownloadsCnt");
	}

	public String getSubDownloadLink() {
		return (String) map.get("SubDownloadLink");
	}

	@Override
	public String toString() {
		return "SearchSubtitlesResult [getIdMovie()=" + getIdMovie()
				+ ", getIDMovieImdb()=" + getIDMovieImdb()
				+ ", getSubDownloadLink()=" + getSubDownloadLink()
				+ ", getSubDownloadsCnt()=" + getSubDownloadsCnt()
				+ ", getTitle()=" + getTitle() + "]";
		// + ", map=" + map + "]";
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchSubtitlesResult other = (SearchSubtitlesResult) obj;
		// title
		if (getTitle() == null) {
			if (other.getTitle() != null)
				return false;
		} else if (!getTitle().equals(other.getTitle()))
			return false;
		// getSubDownloadLink
		if (getSubDownloadLink() == null) {
			if (other.getSubDownloadLink() != null)
				return false;
		} else if (!getSubDownloadLink().equals(other.getSubDownloadLink()))
			return false;
		return true;
	}

}
