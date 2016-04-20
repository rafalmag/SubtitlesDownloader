package pl.rafalmag.howlibwork;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class xmlRcpTest {

    @Test
    public void should_get_xmlRcp() throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://api.opensubtitles.org/xml-rpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Object[] params = new Object[]{}; // new Integer(33), new Integer(9)
        @SuppressWarnings("unchecked")
        Map<String, Object> execute = (Map<String, Object>) client.execute(
                "ServerInfo", params);
        assertThat(execute, Matchers.hasEntry("website_url",
                (Object) "http://www.opensubtitles.org"));
    }

}
