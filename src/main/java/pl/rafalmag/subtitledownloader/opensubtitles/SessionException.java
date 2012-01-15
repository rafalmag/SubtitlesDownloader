package pl.rafalmag.subtitledownloader.opensubtitles;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;

@SuppressWarnings("serial")
public class SessionException extends SubtitlesDownloaderException {

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(Throwable message) {
		super(message);
	}

}
