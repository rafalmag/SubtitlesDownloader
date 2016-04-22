package pl.rafalmag.subtitledownloader.subtitles;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.inject.Singleton;

@Singleton
public class SelectSubtitlesProperties {

    private final ObjectProperty<Subtitles> selectedMovie = new SimpleObjectProperty<>(Subtitles.DUMMY_SUBTITLES);

    public ObjectProperty<Subtitles> selectedSubtitlesProperty() {
        return selectedMovie;
    }

    public void setSelectedSubtitles(Subtitles subtitles) {
        selectedMovie.set(subtitles);
    }

    public Subtitles getSelectedSubtitles() {
        return selectedMovie.get();
    }

}
