package pl.rafalmag.subtitledownloader.title;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectTitleProperties {

	private static final Movie DUMMY_MOVIE = new Movie("", 0, 0);

	private static class SelectTitlePropertiesHolder {
		static SelectTitleProperties instance = new SelectTitleProperties();
	}

	public static SelectTitleProperties getInstance() {
		return SelectTitlePropertiesHolder.instance;
	}

	private SelectTitleProperties() {
	}

	private final ObjectProperty<Movie> selectedMovie = new SimpleObjectProperty<>(
			DUMMY_MOVIE);

	public ObjectProperty<Movie> selectedMovieProperty() {
		return selectedMovie;
	}

	public void setSelectedMovie(Movie movie) {
		selectedMovie.set(movie);
	}

	public Movie getSelectedMovie() {
		return selectedMovie.get();
	}

}
