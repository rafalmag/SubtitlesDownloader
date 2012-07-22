package pl.rafalmag.subtitledownloader.subtitles;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectSubtitlesProperties {

	private static final Subtitles DUMMY_SUBTITLES = new Subtitles("", 0);

	private static class SelectSubtitlesPropertiesHolder {
		static SelectSubtitlesProperties instance = new SelectSubtitlesProperties();
	}

	public static SelectSubtitlesProperties getInstance() {
		return SelectSubtitlesPropertiesHolder.instance;
	}

	private SelectSubtitlesProperties() {
	}

	private final ObjectProperty<Subtitles> selectedMovie = new SimpleObjectProperty<>(
			DUMMY_SUBTITLES);

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
