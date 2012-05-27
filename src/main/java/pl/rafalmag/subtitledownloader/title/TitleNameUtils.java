package pl.rafalmag.subtitledownloader.title;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

public class TitleNameUtils {

	private static final Pattern TITLE = Pattern
			.compile("([a-zA-Z0-9 _.]*)\\..*");
	private static final Pattern TITLE_YEAR = Pattern
			.compile("(.*)[\\{\\[]\\d{4}.*");

	private static final Pattern TITLE_META = Pattern
			.compile("(.*)((BRRIP)|(DVD)|(HD)).*");

	private static final List<Pattern> patternList = ImmutableList.of(
			TITLE_YEAR, TITLE_META, TITLE);

	public static String getTitleFrom(String fileBaseName) {
		String title = checkPatterns(fileBaseName);
		if (title != null) {
			return title.replaceAll("[._]", " ").trim();
		}

		return fileBaseName;
	}

	@Nullable
	private static String checkPatterns(String fileBaseName) {
		for (Pattern pattern : patternList) {
			String title = checkPattern(pattern, fileBaseName);
			if (title != null) {
				return title;
			}
		}
		return null;
	}

	@Nullable
	private static String checkPattern(Pattern pattern, String fileBaseName) {
		Matcher matcher = pattern.matcher(fileBaseName);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

}
