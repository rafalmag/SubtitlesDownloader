package org.opensubtitles;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;

import org.hamcrest.Matchers;
import org.junit.Test;

public class OpenSubtitlesHasherTest {

	@Test
	public void should_calculate_hash() throws Exception {
		// given
		URL resource = this.getClass().getResource("/breakdance.avi");
		File file = new File(resource.toURI());
		// when
		String hashcode = OpenSubtitlesHasher.computeHash(file );
		// then
		assertThat(hashcode, Matchers.equalTo("8e245d9679d31e12"));
	}

}
