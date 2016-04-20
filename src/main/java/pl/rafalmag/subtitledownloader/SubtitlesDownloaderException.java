package pl.rafalmag.subtitledownloader;

@SuppressWarnings("serial")
public class SubtitlesDownloaderException extends Exception {

    public SubtitlesDownloaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubtitlesDownloaderException(String message) {
        super(message);
    }

    public SubtitlesDownloaderException(Throwable message) {
        super(message);
    }

}
