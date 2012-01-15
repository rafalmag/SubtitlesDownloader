package pl.rafalmag.subtitledownloader;

import java.util.Map;

import org.apache.log4j.Logger;

public class SearchSubtitlesResult {

	private static final Logger LOGGER = Logger
			.getLogger(SearchSubtitlesResult.class);

	private final Map<String, Object> map;

	public SearchSubtitlesResult(Map<String, Object> map) {
		this.map = map;
		LOGGER.debug("parsed SearchSubtitlesResult=" + this);
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
