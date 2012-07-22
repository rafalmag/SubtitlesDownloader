package pl.rafalmag.subtitledownloader.subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

public class Subtitles {

	private final IntegerProperty downloadsCount = new SimpleIntegerProperty();
	private final StringProperty fileName = new SimpleStringProperty();

	public Subtitles(SearchSubtitlesResult result) {
		setFileName(result.getSubFileName());
		setDownloadsCount(Integer.parseInt(result.getSubDownloadsCnt()));
	}

	public Subtitles(String fileName, int downloadsCount) {
		setFileName(fileName);
		setDownloadsCount(downloadsCount);
	}

	public IntegerProperty downloadsCountProperty() {
		return downloadsCount;
	}

	public StringProperty fileNameProperty() {
		return fileName;
	}

	public int getDownloadsCount() {
		return downloadsCount.get();
	}

	public void setDownloadsCount(int downloadsCount) {
		this.downloadsCount.set(downloadsCount);
	}

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(String fileName) {
		this.fileName.set(fileName);
	}

	@Override
	public String toString() {
		return "Subtitles [getFileName()=" + getFileName()
				+ ", getDownloadsCount()=" + getDownloadsCount() + "]";
	}

}
