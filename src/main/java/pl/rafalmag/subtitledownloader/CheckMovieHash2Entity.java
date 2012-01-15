package pl.rafalmag.subtitledownloader;

public class CheckMovieHash2Entity {
	String movieHash;
	int imdbId;
	String movieName;
	int year;
	int seenCount;

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

}
