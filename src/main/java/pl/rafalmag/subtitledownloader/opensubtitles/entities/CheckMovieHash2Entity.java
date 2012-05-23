package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckMovieHash2Entity {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CheckMovieHash2Entity.class);

	String movieHash;
	int imdbId;
	String movieName;
	int year;
	int seenCount;

	public CheckMovieHash2Entity(Map<String, Object> record) {
		movieHash = (String) record.get("MovieHash");
		imdbId = objectToInt(record.get("MovieImdbID"));
		movieName = (String) record.get("MovieName");
		year = objectToInt(record.get("MovieYear"));
		seenCount = objectToInt(record.get("SeenCount"));
		LOGGER.debug("parsed checkMovieHash2Entity={}", this);
	}

	private int objectToInt(Object intObject) {
		if (intObject instanceof Integer) {
			return (Integer) intObject;
		} else if (intObject instanceof String) {
			return Integer.parseInt((String) intObject);
		} else {
			throw new IllegalStateException("Unsupported object type: "
					+ intObject);
		}
	}

	public CheckMovieHash2Entity(String movieHash, int imdbId,
			String movieName, int year, int seenCount) {
		this.movieHash = movieHash;
		this.imdbId = imdbId;
		this.movieName = movieName;
		this.year = year;
		this.seenCount = seenCount;
	}

	public String getMovieHash() {
		return movieHash;
	}

	public void setMovieHash(String movieHash) {
		this.movieHash = movieHash;
	}

	public int getImdbId() {
		return imdbId;
	}

	public void setImdbId(int imdbId) {
		this.imdbId = imdbId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSeenCount() {
		return seenCount;
	}

	public void setSeenCount(int seenCount) {
		this.seenCount = seenCount;
	}

	@Override
	public String toString() {
		return "CheckMovieHash2Entity [movieHash=" + movieHash + ", imdbId="
				+ imdbId + ", movieName=" + movieName + ", year=" + year
				+ ", seenCount=" + seenCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + imdbId;
		result = prime * result
				+ ((movieHash == null) ? 0 : movieHash.hashCode());
		result = prime * result
				+ ((movieName == null) ? 0 : movieName.hashCode());
		result = prime * result + seenCount;
		result = prime * result + year;
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
		CheckMovieHash2Entity other = (CheckMovieHash2Entity) obj;
		if (imdbId != other.imdbId)
			return false;
		if (movieHash == null) {
			if (other.movieHash != null)
				return false;
		} else if (!movieHash.equals(other.movieHash))
			return false;
		if (movieName == null) {
			if (other.movieName != null)
				return false;
		} else if (!movieName.equals(other.movieName))
			return false;
		if (seenCount != other.seenCount)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}
