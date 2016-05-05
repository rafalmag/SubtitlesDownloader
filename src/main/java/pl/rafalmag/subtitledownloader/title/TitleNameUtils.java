package pl.rafalmag.subtitledownloader.title;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleNameUtils {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TitleNameUtils.class);

    private static final Pattern TITLE = Pattern
            .compile("^([a-zA-Z0-9 _\\.]*)\\..*");

    private static final Pattern TITLE_WITH_DASH = Pattern
            .compile("^([a-zA-Z0-9 _\\.]*)-.*");

    private static final Pattern TITLE_YEAR = Pattern
            .compile("^([a-zA-Z0-9 _\\.]*)([\\{\\[\\(])?\\d{4}[^p].*");

    private static final Pattern TITLE_META = Pattern
            .compile("^([a-zA-Z0-9 _\\.]*)([\\{\\[\\(])?((BRRIP)|(DVD)|(HD)).*");

    // order is important
    private static final List<Pattern> patternList = ImmutableList.of(TITLE_YEAR, TITLE_META, TITLE_WITH_DASH, TITLE);

    public static String getTitleFrom(String fileName) {
        String fileBaseName = FilenameUtils.getBaseName(fileName);
        Optional<String> title = checkPatterns(fileBaseName);
        if (title.isPresent()) {
            return prepareResult(title.get());
        } else {
            return prepareResult(fileBaseName);
        }
    }

    private static String prepareResult(String title) {
        return title.replaceAll("[._]", " ").trim();
    }

    private static Optional<String> checkPatterns(String fileBaseName) {
        for (Pattern pattern : patternList) {
            Optional<String> title = checkPattern(pattern, fileBaseName);
            if (title.isPresent()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("checkPatterns matched\n" + "fileBaseName:   "
                            + fileBaseName + "\nmatched by:     " + pattern
                            + "\nreturned title: " + title);
                }
                return title;
            }
        }
        return Optional.empty();
    }

    private static Optional<String> checkPattern(Pattern pattern, String fileBaseName) {
        Matcher matcher = pattern.matcher(fileBaseName);
        if (matcher.find()) {
            String stringToBeReturned = matcher.group(1).trim();
            if (stringToBeReturned.isEmpty()) {
                LOGGER.error("checkPattern matched\n" + "fileBaseName:   "
                        + fileBaseName + "\nmatched by:     " + pattern
                        + "\nreturned empty string");
                return Optional.empty();
            }
            return Optional.of(stringToBeReturned);
        }
        return Optional.empty();
    }

}
