package pl.rafalmag.subtitledownloader;

import org.junit.Test;

public class SessionTest {

	@Test
	public void should_login() throws Exception {
		Session session = new Session();
		try {
			session.login();
		} finally {
			session.logout();
		}
	}

}
