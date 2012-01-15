package pl.rafalmag.subtitledownloader.opensubtitles.entities;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

public class ImdbMovieDetails {

	private static final Logger LOGGER = Logger
			.getLogger(ImdbMovieDetails.class);

	Map<String, String> cast;
	double rating;
	String coverUrl;
	int id;
	int votes;
	String title;
	Collection<String> aka;
	int year;

	public ImdbMovieDetails(Map<String, Object> data) {
		// imdbMovieDetails.setCast(data.get("cast")); // TODO
		// imdbMovieDetails.setAka(data.get("aka")); // TODO
		rating = Double.parseDouble((String) data.get("rating"));
		coverUrl = (String) data.get("cover");
		id = Integer.parseInt((String) data.get("id"));
		votes = Integer.parseInt((String) data.get("votes"));
		title = (String) data.get("title");
		year = Integer.parseInt((String) data.get("year"));
		LOGGER.debug("parsed imdbMovieDetails=" + this);
	}

	public Map<String, String> getCast() {
		return cast;
	}

	public void setCast(Map<String, String> cast) {
		this.cast = cast;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Collection<String> getAka() {
		return aka;
	}

	public void setAka(Collection<String> aka) {
		this.aka = aka;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "ImdbMovieDetails [cast=" + cast + ", rating=" + rating
				+ ", coverUrl=" + coverUrl + ", id=" + id + ", votes=" + votes
				+ ", title=" + title + ", aka=" + aka + ", year=" + year + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aka == null) ? 0 : aka.hashCode());
		result = prime * result + ((cast == null) ? 0 : cast.hashCode());
		result = prime * result
				+ ((coverUrl == null) ? 0 : coverUrl.hashCode());
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + votes;
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
		ImdbMovieDetails other = (ImdbMovieDetails) obj;
		if (aka == null) {
			if (other.aka != null)
				return false;
		} else if (!aka.equals(other.aka))
			return false;
		if (cast == null) {
			if (other.cast != null)
				return false;
		} else if (!cast.equals(other.cast))
			return false;
		if (coverUrl == null) {
			if (other.coverUrl != null)
				return false;
		} else if (!coverUrl.equals(other.coverUrl))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(rating) != Double
				.doubleToLongBits(other.rating))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (votes != other.votes)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}
