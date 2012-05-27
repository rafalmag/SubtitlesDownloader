package pl.rafalmag.subtitledownloader.title;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class TitleNameUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TitleNameUtils.class);

	private static final Pattern TITLE = Pattern
			.compile("^([a-zA-Z0-9 _\\.]*)\\..*");

	private static final Pattern TITLE_WITH_DASH = Pattern
			.compile("^([a-zA-Z0-9 _\\.]*)-.*");

	private static final Pattern TITLE_YEAR = Pattern
			.compile("^([a-zA-Z0-9 _\\.]*)([\\{\\[\\(])?\\d{4}.*");

	private static final Pattern TITLE_META = Pattern
			.compile("^([a-zA-Z0-9 _\\.]*)([\\{\\[\\(])?((BRRIP)|(DVD)|(HD)).*");

	// order is important
	private static final List<Pattern> patternList = ImmutableList.of(
			TITLE_YEAR, TITLE_META, TITLE_WITH_DASH, TITLE);

	public static String getTitleFrom(String fileName) {
		String fileBaseName = FilenameUtils.getBaseName(fileName);
		String title = checkPatterns(fileBaseName);
		if (title != null) {
			return prepareResult(title);
		}

		return prepareResult(fileBaseName);
	}

	private static String prepareResult(String title) {
		return title.replaceAll("[._]", " ").trim();
	}

	@Nullable
	private static String checkPatterns(String fileBaseName) {
		for (Pattern pattern : patternList) {
			String title = checkPattern(pattern, fileBaseName);
			if (title != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("checkPatterns matched\n" + "fileBaseName:   "
							+ fileBaseName + "\nmatched by:     " + pattern
							+ "\nreturned title: " + title);
				}
				return title;
			}
		}
		return null;
	}

	@Nullable
	private static String checkPattern(Pattern pattern, String fileBaseName) {
		Matcher matcher = pattern.matcher(fileBaseName);
		if (matcher.find()) {
			String stringToBeReturned = matcher.group(1).trim();
			if (stringToBeReturned.isEmpty()) {
				LOGGER.error("checkPattern matched\n" + "fileBaseName:   "
						+ fileBaseName + "\nmatched by:     " + pattern
						+ "\nreturned empty string");
				return null;
			}
			return stringToBeReturned;
		}
		return null;
	}

}
