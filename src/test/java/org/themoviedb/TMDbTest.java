package org.themoviedb;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TMDbTest {

	private static String TM_DB_API_KEY = "TODO"; // TODO waiting for api key
													// approval

	@Test
	public void should_auth() throws Exception {

		// rest, json
		// given
		String get_auth = "http://api.themoviedb.org/3/movie/11?api_key="
				+ TM_DB_API_KEY;

		DefaultClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);

		WebResource resource = client.resource(get_auth);
		// lets get the XML as a String
		String text = resource.type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		System.out.println(text);
		// when

		// then

	}
}
