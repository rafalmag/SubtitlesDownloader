package org.themoviedb;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Test;
import pl.rafalmag.subtitledownloader.themoviedb.TheMovieDbHelper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TMDbTest {

    private static String TM_DB_API_KEY = TheMovieDbHelper.API_KEY;

    @Test
    public void should_get_starWars() throws Exception {
        // given
        String get_movie11 = "http://api.themoviedb.org/3/movie/11?api_key="
                + TM_DB_API_KEY;

        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create(clientConfig);

        // when
        WebResource resource = client.resource(get_movie11);
        // lets get the XML as a String
        String text = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON).get(String.class);

        // then
        assertThat(text, containsString("Star Wars"));
        // System.out.println(text);
    }

    @Test
    public void should_get_conf() throws Exception {
        // given
        String get_conf = "http://api.themoviedb.org/3/configuration?api_key="
                + TM_DB_API_KEY;

        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create(clientConfig);

        // when
        WebResource resource = client.resource(get_conf);
        // lets get the XML as a String
        String text = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON).get(String.class);

        // then
        assertThat(text, containsString("_sizes"));
    }

}
