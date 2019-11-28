package com.adobe.assignment.http.endtoend;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.adobe.assignment.http.testutils.ClientConfigUtil;
import com.adobe.assignment.http.testutils.Constants;
import com.adobe.assignment.http.testutils.ServerRunner;

@Ignore public class GetMethodHandlerTest { // extends TestCase {

    private HttpClient client;
    private PoolingHttpClientConnectionManager connMgr;
    static private URI indexUri;
    static private ServerRunner serverRunner;

    // private HttpServer server;

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
        // super.setUp();

        connMgr = ClientConfigUtil.createMultiThreadedHttpConnectionManager();

        client = HttpClients.custom().setConnectionManager(connMgr).build();

    }

    @Test
    public void testGetExistingRequest( ) throws Exception {
        System.out.println(indexUri.toString());
        HttpGet method = new HttpGet(indexUri.toString());
        HttpResponse response = client.execute(method);
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_OK));
    }

    /**
     * Test getting a resource that does not exist on the server. In this case,
     * a status code of 404 (NOT FOUND) should be returned.
     * 
     */
    @Test
    public void testGetNonExistingResource( ) throws Exception {
        URI uri = ClientConfigUtil.createDefaultUriWithPath("/NotExistingResource");
        HttpGet method = new HttpGet(uri.toString());
        HttpResponse response = client.execute(method);
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testMethodNotImplemented( ) throws Exception {
        HttpOptions method = new HttpOptions(indexUri.toString());
        HttpResponse response = client.execute(method);
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_NOT_IMPLEMENTED));
    }

    @Test
    public void testConcurrentGetRequests( ) throws Exception {
        // All get requests to /index.html in the server's web-root should
        // return success.
        GetThread[] threads = new GetThread[50];
        for (int i = 0; i < threads.length; i++) {
            HttpGet httpget = new HttpGet(indexUri.toString());
            threads[i] = new GetThread(client, httpget);
        }

        // start the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }

        // join the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }
    }

    static class GetThread extends Thread {

        private final HttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private File indexFile = new File(Constants.SERVER_ROOT_TEST, Constants.INDEX_FILE);

        public GetThread(HttpClient httpClient, HttpGet httpget) {
            this.httpClient = httpClient;
            this.context = HttpClientContext.create();
            this.httpget = httpget;
        }

        @Override
        public void run( ) {
            try {
                HttpResponse response = httpClient.execute(httpget, context);

                HttpEntity entity = response.getEntity();
                assertThat(entity.getContentLength(), is(indexFile.length()));
                // entity.getContentLength() == indexFile.

            } catch (ClientProtocolException ex) {
                // Handle protocol errors
            } catch (IOException ex) {
                // Handle I/O errors
            }
        }

    }
    
}