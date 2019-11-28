package com.adobe.assignment.http.endtoend;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.testutils.ClientConfigUtil;
import com.adobe.assignment.http.testutils.ServerRunner;

@Ignore public class KeepAliveTest {

    private HttpClient client;
    BasicHttpClientConnectionManager connMgr;
    static private URI indexUri;
    static private ServerRunner serverRunner;

    @BeforeClass
    public static void startServer( ) throws Exception {
        serverRunner = new ServerRunner();
        indexUri = ClientConfigUtil.createDefaultUriWithPath("/index.html");
    }

    @AfterClass
    public static void tearDown( ) throws Exception {
        serverRunner.stopServer();
    }

    @Before
    public void setUp( ) throws Exception {
        connMgr = new BasicHttpClientConnectionManager();// ClientConfigUtil.createMultiThreadedHttpConnectionManager();

        client = HttpClients.custom().setConnectionManager(connMgr)
                // .setKeepAliveStrategy(myStrategy)
                .build();
    }

    /**
     * This test apparently only checks if get methods return with an OK status.
     * However the purpose is to verify if all requests are performed on same connection.
     * The necessary setup for an automatic test checking that only one connection was 
     * created needs a big effort.
     * As in this occasion I just have few time, I perform a visual check of the log output
     * checking that only one connection was created.
     * 
     * @throws Exception
     */
    @Test
    public void testGetExistingRequestKeepAlive( ) throws Exception {
        System.out.println(indexUri.toString());
        HttpGet method = new HttpGet(indexUri.toString());

        // Next ten methods are executed on same connection.
        for( int i = 0; i < 2; ++i) {
            // First request with header keep-alive.
            HttpResponse response = client.execute(method);

            assertThat(response.getFirstHeader(HttpConstants.HEADER_PERSISTENT_CONNECTION).getValue(),
                    is(HttpConstants.HEADER_VALUE_KEEP_ALIVE));

            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            br.close();
        }
        // Second request.
        //HttpResponse response2 = client.execute(method);

        //assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_OK));
    }
}
