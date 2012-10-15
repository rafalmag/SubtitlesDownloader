package pl.rafalmag.subtitledownloader.subtitles;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class DownloaderTest {

	@Test
	public void should_get_subtitles_destination_path() throws Exception {
		// given
		String input = "a.avi";
		String extension = "sub";
		Path expectedDestinationPath = Paths.get("a.sub");

		// when
		Path destinationPath = DownloaderTask.getSubtitlesDestinationPath(
				input,
				extension);

		// then
		assertThat(destinationPath, equalTo(expectedDestinationPath));
	}

}
