package pl.rafalmag.subtitledownloader.themoviedb;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.omertron.themoviedbapi.TheMovieDbApi;

public class ApiKeyMaskingPatternLayout extends PatternLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        return message.replace(TheMovieDbService.API_KEY,"[API-KEY]");
    }
}
