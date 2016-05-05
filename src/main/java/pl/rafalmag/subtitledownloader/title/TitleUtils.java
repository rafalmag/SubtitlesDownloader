package pl.rafalmag.subtitledownloader.title;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitleUtils.class);

    // non digits optional, then group of digits
    private static final Pattern IMDB_PATTERN = Pattern.compile("\\D*(\\d+).*");

    public static int getImdbFromString(@Nullable String imdbStr) {
        if (Strings.isNullOrEmpty(imdbStr)) {
            LOGGER.debug("imdbId is null");
            return -1;
        }
        Matcher matcher = IMDB_PATTERN.matcher(imdbStr);
        if (matcher.find()) {
            String digits = matcher.group(1);
            return Integer.parseInt(digits);
        } else {
            LOGGER.warn("imdbId (" + imdbStr + ") doesn't match " + IMDB_PATTERN);
            return -1;
        }
    }
}