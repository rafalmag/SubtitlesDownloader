package org.themoviedb;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TMDbTest {

	private static String TM_DB_API_KEY = "d59492cb5d91e31ca1832ce5c447a099";

	@Test
	public void should_auth() throws Exception {

		// rest, json
		// given
		String get_auth = "http://api.themoviedb.org/3/movie/11?api_key="
				+ TM_DB_API_KEY;

		DefaultClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);

		// when
		WebResource resource = client.resource(get_auth);
		// lets get the XML as a String
		String text = resource.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON).get(String.class);

		// then
		assertThat(text, containsString("Star Wars"));
	}

	@Test
	public void should_get_conf() throws Exception {

		// rest, json
		// given
		String get_auth = "http://api.themoviedb.org/3/configuration?api_key="
				+ TM_DB_API_KEY;

		DefaultClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);

		// when
		WebResource resource = client.resource(get_auth);
		// lets get the XML as a String
		String text = resource.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON).get(String.class);

		// then
		assertThat(text, containsString("_sizes"));

	}
}
