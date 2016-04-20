package pl.rafalmag.subtitledownloader.subtitles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;

public class Subtitles implements Comparable<Subtitles> {

	public static final Subtitles DUMMY_SUBTITLES = new Subtitles("", 0) {
		@Override
		public String toString() {
			return "";
		}
	};

	private final IntegerProperty downloadsCount = new SimpleIntegerProperty();
	private final StringProperty fileName = new SimpleStringProperty();
	private final StringProperty downloadLink = new SimpleStringProperty();

	public Subtitles(SearchSubtitlesResult result) {
		setFileName(result.getSubFileName());
		setDownloadsCount(Integer.parseInt(result.getSubDownloadsCnt()));
		setDownloadLink(result.getSubDownloadLink());
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

	public StringProperty downloadLinkProperty() {
		return downloadLink;
	}

	public String getDownloadLink() {
		return downloadLink.get();
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink.set(downloadLink);
	}

	@Override
	public int compareTo(Subtitles o) {
		return new Integer(getDownloadsCount()).compareTo(o.getDownloadsCount());
	}

}
