package pl.rafalmag.subtitledownloader.subtitles;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DownloaderTest {

	@Test
	public void should_get_subtitles_destination_path() throws Exception {
		// given
		String input = "c:\\a.avi";
		String extension = "sub";
		String expectedDestinationPath = "c:\\a.sub";

		// when
		String destinationPath = Downloader.getSubtitlesDestinationPath(input,
				extension);

		// then
		assertThat(destinationPath, equalTo(expectedDestinationPath));
	}

}
