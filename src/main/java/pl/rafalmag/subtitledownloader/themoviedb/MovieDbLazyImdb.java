package pl.rafalmag.subtitledownloader.themoviedb;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.enumeration.MediaType;
import com.omertron.themoviedbapi.enumeration.MovieMethod;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.Language;
import com.omertron.themoviedbapi.model.artwork.Artwork;
import com.omertron.themoviedbapi.model.change.ChangeKeyItem;
import com.omertron.themoviedbapi.model.collection.Collection;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.credits.MediaCreditCrew;
import com.omertron.themoviedbapi.model.keyword.Keyword;
import com.omertron.themoviedbapi.model.list.UserList;
import com.omertron.themoviedbapi.model.media.AlternativeTitle;
import com.omertron.themoviedbapi.model.media.MediaCreditList;
import com.omertron.themoviedbapi.model.media.Translation;
import com.omertron.themoviedbapi.model.media.Video;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.movie.ProductionCompany;
import com.omertron.themoviedbapi.model.movie.ProductionCountry;
import com.omertron.themoviedbapi.model.movie.ReleaseInfo;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.*;

import java.util.List;

public class MovieDbLazyImdb extends MovieInfo {
    private volatile MovieInfo movieDb;

    public MovieDbLazyImdb(MovieInfo movieDb) {
        this.movieDb = movieDb;
    }

    @Override
    public String getImdbID() {
        String imdbID = movieDb.getImdbID();
        if (imdbID == null) {
            // MovieInfo retrieved from search lacks a lot of data
            reInit();
            return movieDb.getImdbID();
        }
        return imdbID;
    }

    /**
     * This method will get all fields, as MovieInfo retrieved from search lacks a lot of data.
     */
    private void reInit() {
        try {
            movieDb = TheMovieDbHelper.getInstance().getFullMovieDb(this);
        } catch (MovieDbException e) {
            throw new IllegalStateException("Could not init MovieDb, because of " + e.getMessage(), e);
        }
    }

    @Override
    public Collection getBelongsToCollection() {
        return movieDb.getBelongsToCollection();
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
    public List<ProductionCompany> getProductionCompanies() {
        return movieDb.getProductionCompanies();
    }

    @Override
    public List<ProductionCountry> getProductionCountries() {
        return movieDb.getProductionCountries();
    }

    @Override
    public String getOverview() {
        return movieDb.getOverview();
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
    public String getStatus() {
        return movieDb.getStatus();
    }

    @Override
    public void setBelongsToCollection(Collection belongsToCollection) {
        movieDb.setBelongsToCollection(belongsToCollection);
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
    public MediaType getMediaType() {
        return movieDb.getMediaType();
    }

    @Override
    public void setMediaType(String mediaType) {
        movieDb.setMediaType(mediaType);
    }

    @Override
    public void setMediaType(MediaType mediaType) {
        movieDb.setMediaType(mediaType);
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
    public boolean isVideo() {
        return movieDb.isVideo();
    }

    @Override
    public void setVideo(Boolean video) {
        movieDb.setVideo(video);
    }

    @Override
    public float getUserRating() {
        return movieDb.getUserRating();
    }

    @Override
    public void setUserRating(float userRating) {
        movieDb.setUserRating(userRating);
    }

    @Override
    public List<Integer> getGenreIds() {
        return movieDb.getGenreIds();
    }

    @Override
    public void setGenreIds(List<Integer> genreIds) {
        movieDb.setGenreIds(genreIds);
    }

    @Override
    public String getOriginalLanguage() {
        return movieDb.getOriginalLanguage();
    }

    @Override
    public void setOriginalLanguage(String originalLanguage) {
        movieDb.setOriginalLanguage(originalLanguage);
    }

    @Override
    public void setAdult(boolean adult) {
        movieDb.setAdult(adult);
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
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        movieDb.setProductionCompanies(productionCompanies);
    }

    @Override
    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        movieDb.setProductionCountries(productionCountries);
    }

    @Override
    public void setOverview(String overview) {
        movieDb.setOverview(overview);
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
    public void setStatus(String status) {
        movieDb.setStatus(status);
    }

    @Override
    public List<AlternativeTitle> getAlternativeTitles() {
        return movieDb.getAlternativeTitles();
    }

    @Override
    public List<MediaCreditCast> getCast() {
        return movieDb.getCast();
    }

    @Override
    public List<MediaCreditCrew> getCrew() {
        return movieDb.getCrew();
    }

    @Override
    public List<Artwork> getImages() {
        return movieDb.getImages();
    }

    @Override
    public List<Keyword> getKeywords() {
        return movieDb.getKeywords();
    }

    @Override
    public List<ReleaseInfo> getReleases() {
        return movieDb.getReleases();
    }

    @Override
    public List<Video> getVideos() {
        return movieDb.getVideos();
    }

    @Override
    public List<Translation> getTranslations() {
        return movieDb.getTranslations();
    }

    @Override
    public List<MovieInfo> getSimilarMovies() {
        return movieDb.getSimilarMovies();
    }

    @Override
    public List<UserList> getLists() {
        return movieDb.getLists();
    }

    @Override
    public List<Review> getReviews() {
        return movieDb.getReviews();
    }

    @Override
    public List<ChangeKeyItem> getChanges() {
        return movieDb.getChanges();
    }

    @Override
    public void setAlternativeTitles(WrapperAlternativeTitles alternativeTitles) {
        movieDb.setAlternativeTitles(alternativeTitles);
    }

    @Override
    public void setCredits(MediaCreditList credits) {
        movieDb.setCredits(credits);
    }

    @Override
    public void setImages(WrapperImages images) {
        movieDb.setImages(images);
    }

    @Override
    public void setKeywords(WrapperMovieKeywords keywords) {
        movieDb.setKeywords(keywords);
    }

    @Override
    public void setReleases(WrapperReleaseInfo releases) {
        movieDb.setReleases(releases);
    }

    @Override
    public void setVideos(WrapperVideos trailers) {
        movieDb.setVideos(trailers);
    }

    @Override
    public void setTranslations(WrapperTranslations translations) {
        movieDb.setTranslations(translations);
    }

    @Override
    public void setSimilarMovies(WrapperGenericList<MovieInfo> similarMovies) {
        movieDb.setSimilarMovies(similarMovies);
    }

    @Override
    public void setLists(WrapperGenericList<UserList> lists) {
        movieDb.setLists(lists);
    }

    @Override
    public void setReviews(WrapperGenericList<Review> reviews) {
        movieDb.setReviews(reviews);
    }

    @Override
    public void setChanges(WrapperChanges changes) {
        movieDb.setChanges(changes);
    }

    @Override
    public boolean hasMethod(MovieMethod method) {
        return movieDb.hasMethod(method);
    }

    @Override
    public String getMediaId() {
        return movieDb.getMediaId();
    }

    @Override
    public void setMediaId(String mediaId) {
        movieDb.setMediaId(mediaId);
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
