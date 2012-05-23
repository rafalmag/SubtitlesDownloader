package pl.rafalmag.subtitledownloader.themoviedb;

import java.util.List;

import com.moviejukebox.themoviedb.model.Collection;
import com.moviejukebox.themoviedb.model.Genre;
import com.moviejukebox.themoviedb.model.Language;
import com.moviejukebox.themoviedb.model.MovieDb;
import com.moviejukebox.themoviedb.model.ProductionCompany;
import com.moviejukebox.themoviedb.model.ProductionCountry;

public class MovieDbLazyImdb extends MovieDb {
	private volatile MovieDb movieDb;

	public MovieDbLazyImdb(MovieDb movieDb) {
		this.movieDb = movieDb;
	}

	private void reInit() {
		movieDb = TheMovieDbHelper.getInstance().getFullMovieDb(getId());
	}

	@Override
	public String getBackdropPath() {
		return movieDb.getBackdropPath();
	}

	@Override
	public int getId() {
		return movieDb.getId();
	}

	@Override
	public String getOriginalTitle() {
		return movieDb.getOriginalTitle();
	}

	@Override
	public float getPopularity() {
		return movieDb.getPopularity();
	}

	@Override
	public String getPosterPath() {
		return movieDb.getPosterPath();
	}

	@Override
	public String getReleaseDate() {
		return movieDb.getReleaseDate();
	}

	@Override
	public String getTitle() {
		return movieDb.getTitle();
	}

	@Override
	public boolean isAdult() {
		return movieDb.isAdult();
	}

	@Override
	public Collection getBelongsToCollection() {
		return movieDb.getBelongsToCollection();
	}

	@Override
	public long getBudget() {
		return movieDb.getBudget();
	}

	@Override
	public List<Genre> getGenres() {
		return movieDb.getGenres();
	}

	@Override
	public String getHomepage() {
		return movieDb.getHomepage();
	}

	@Override
	public String getImdbID() {
		String imdbID = movieDb.getImdbID();
		if (imdbID == null) {
			reInit();
			return movieDb.getImdbID();
		}
		return imdbID;
	}

	@Override
	public String getOverview() {
		return movieDb.getOverview();
	}

	@Override
	public List<ProductionCompany> getProductionCompanies() {
		return movieDb.getProductionCompanies();
	}

	@Override
	public List<ProductionCountry> getProductionCountries() {
		return movieDb.getProductionCountries();
	}

	@Override
	public long getRevenue() {
		return movieDb.getRevenue();
	}

	@Override
	public int getRuntime() {
		return movieDb.getRuntime();
	}

	@Override
	public List<Language> getSpokenLanguages() {
		return movieDb.getSpokenLanguages();
	}

	@Override
	public String getTagline() {
		return movieDb.getTagline();
	}

	@Override
	public float getVoteAverage() {
		return movieDb.getVoteAverage();
	}

	@Override
	public int getVoteCount() {
		return movieDb.getVoteCount();
	}

	@Override
	public void setBackdropPath(String backdropPath) {
		movieDb.setBackdropPath(backdropPath);
	}

	@Override
	public void setId(int id) {
		movieDb.setId(id);
	}

	@Override
	public void setOriginalTitle(String originalTitle) {
		movieDb.setOriginalTitle(originalTitle);
	}

	@Override
	public void setPopularity(float popularity) {
		movieDb.setPopularity(popularity);
	}

	@Override
	public void setPosterPath(String posterPath) {
		movieDb.setPosterPath(posterPath);
	}

	@Override
	public void setReleaseDate(String releaseDate) {
		movieDb.setReleaseDate(releaseDate);
	}

	@Override
	public void setTitle(String title) {
		movieDb.setTitle(title);
	}

	@Override
	public void setAdult(boolean adult) {
		movieDb.setAdult(adult);
	}

	@Override
	public void setBelongsToCollection(Collection belongsToCollection) {
		movieDb.setBelongsToCollection(belongsToCollection);
	}

	@Override
	public void setBudget(long budget) {
		movieDb.setBudget(budget);
	}

	@Override
	public void setGenres(List<Genre> genres) {
		movieDb.setGenres(genres);
	}

	@Override
	public void setHomepage(String homepage) {
		movieDb.setHomepage(homepage);
	}

	@Override
	public void setImdbID(String imdbID) {
		movieDb.setImdbID(imdbID);
	}

	@Override
	public void setOverview(String overview) {
		movieDb.setOverview(overview);
	}

	@Override
	public void setProductionCompanies(
			List<ProductionCompany> productionCompanies) {
		movieDb.setProductionCompanies(productionCompanies);
	}

	@Override
	public void setProductionCountries(
			List<ProductionCountry> productionCountries) {
		movieDb.setProductionCountries(productionCountries);
	}

	@Override
	public void setRevenue(long revenue) {
		movieDb.setRevenue(revenue);
	}

	@Override
	public void setRuntime(int runtime) {
		movieDb.setRuntime(runtime);
	}

	@Override
	public void setSpokenLanguages(List<Language> spokenLanguages) {
		movieDb.setSpokenLanguages(spokenLanguages);
	}

	@Override
	public void setTagline(String tagline) {
		movieDb.setTagline(tagline);
	}

	@Override
	public void setVoteAverage(float voteAverage) {
		movieDb.setVoteAverage(voteAverage);
	}

	@Override
	public void setVoteCount(int voteCount) {
		movieDb.setVoteCount(voteCount);
	}

	@Override
	public void handleUnknown(String key, Object value) {
		movieDb.handleUnknown(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		return movieDb.equals(obj);
	}

	@Override
	public int hashCode() {
		return movieDb.hashCode();
	}

	@Override
	public String toString() {
		return movieDb.toString();
	}

}
