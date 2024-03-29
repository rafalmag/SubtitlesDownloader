package pl.rafalmag.subtitledownloader.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;

import javax.annotation.Nullable;
import java.io.File;

@Singleton
public class SelectMovieProperties {

    public static final String NO_MOVIE_SELECTED = "";

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    private final StringProperty filePath = new SimpleStringProperty(NO_MOVIE_SELECTED);

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public File getFile() {
        return new File(getFilePath());
    }

    public void setFile(File file) {
        setFilePath(file.getPath());
    }

    public StringProperty movieFileProperty() {
        return filePath;
    }

    @Nullable
    public File getInitialDir() {
        return subtitlesDownloaderProperties.getInitialDir().orElse(null);
    }

    public void setInitialDir(@Nullable File initialDir) {
        // looks redundant, but as here is a getter, it is good to have setter as well
        subtitlesDownloaderProperties.setInitialDir(initialDir);
    }

}
