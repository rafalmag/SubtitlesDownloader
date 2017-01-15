package pl.rafalmag.subtitledownloader.opensubtitles;


public class UnauthorizedSessionException extends SessionException {
    public UnauthorizedSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedSessionException(String message) {
        super(message);
    }

    public UnauthorizedSessionException(Throwable message) {
        super(message);
    }
}
