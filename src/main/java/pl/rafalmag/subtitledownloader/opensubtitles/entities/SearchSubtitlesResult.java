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

	public String getMovieName() {
		return (String) map.get("MovieName");
	}

	@Override
	public String toString() {
		return "SearchSubtitlesResult [map=" + map + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
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
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

}
