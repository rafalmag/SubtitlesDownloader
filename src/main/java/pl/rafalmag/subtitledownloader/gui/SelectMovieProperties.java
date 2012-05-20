package pl.rafalmag.subtitledownloader.gui;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.annotation.Nullable;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;

public class SelectMovieProperties {

	private static class SelectMoviePropertiesHolder {
		static SelectMovieProperties instance = new SelectMovieProperties();
	}

	public static SelectMovieProperties getInstance() {
		return SelectMoviePropertiesHolder.instance;
	}

	private final StringProperty filePath = new SimpleStringProperty("");

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
		return SubtitlesDownloaderProperties.getInstance().getInitialDir();
	}

	public void setInitialDir(@Nullable File initialDir) {
		SubtitlesDownloaderProperties.getInstance().setInitialDir(initialDir);
	}

}
