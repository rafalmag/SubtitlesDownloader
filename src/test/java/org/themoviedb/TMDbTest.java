package org.themoviedb;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbService;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TMDbTest {

    private static String TM_DB_API_KEY = TheMovieDbService.API_KEY;

    @Test
    public void should_get_starWars() throws Exception {
        // given
        String get_movie11 = "http://api.themoviedb.org/3/movie/11?api_key="
                + TM_DB_API_KEY;

        ClientConfig clientConfig = new ClientConfig();
        try (Client client = ClientBuilder.newClient(clientConfig)) {
            // when
            WebTarget resource = client.target(get_movie11);
            // lets get the XML as a String
            String text = resource.request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON).get(String.class);

            // then
            assertThat(text, containsString("Star Wars"));
            // System.out.println(text);
        }
    }

    @Test
    public void should_get_conf() throws Exception {
        // given
        String get_conf = "http://api.themoviedb.org/3/configuration?api_key="
                + TM_DB_API_KEY;

        ClientConfig clientConfig = new ClientConfig();
        try (Client client = ClientBuilder.newClient(clientConfig)) {

            // when
            WebTarget resource = client.target(get_conf);
            // lets get the XML as a String
            String text = resource.request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON).get(String.class);

            // then
            assertThat(text, containsString("_sizes"));
        }
    }

}
