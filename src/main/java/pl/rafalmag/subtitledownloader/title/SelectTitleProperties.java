package pl.rafalmag.subtitledownloader.title;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.rafalmag.subtitledownloader.subtitles.SelectSubtitlesProperties;
import pl.rafalmag.subtitledownloader.subtitles.Subtitles;

@Singleton
public class SelectTitleProperties {

    @Inject
    private SelectSubtitlesProperties selectSubtitlesProperties;

    private final ObjectProperty<Movie> selectedMovie = new SimpleObjectProperty<>(Movie.DUMMY_MOVIE);

    public ObjectProperty<Movie> selectedMovieProperty() {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie movie) {
        selectedMovie.set(movie);
    }

    public Movie getSelectedMovie() {
        return selectedMovie.get();
    }

    public SelectTitleProperties() {
        // changing of movie invalidates the selected subtitle
        selectedMovie.addListener(observable -> selectSubtitlesProperties.setSelectedSubtitles(Subtitles.DUMMY_SUBTITLES));
    }

}
