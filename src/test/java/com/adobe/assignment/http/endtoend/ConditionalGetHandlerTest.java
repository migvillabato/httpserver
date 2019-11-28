package com.adobe.assignment.http.endtoend;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.adobe.assignment.http.testutils.ClientConfigUtil;
import com.adobe.assignment.http.testutils.Constants;
import com.adobe.assignment.http.testutils.ServerRunner;
import com.adobe.assignment.http.utils.FileUtil;
import com.adobe.assignment.http.utils.HeaderFormat;

@Ignore public class ConditionalGetHandlerTest { // extends TestCase {

    private HttpClient client;
    private static URI indexUri;
    private BasicHttpClientConnectionManager connMgr;
    private File indexFile;
    static private ServerRunner serverRunner;

    @BeforeClass
    public static void startServer( ) throws IOException, URISyntaxException {
        serverRunner = new ServerRunner();
        indexUri = ClientConfigUtil.createDefaultUriWithPath("/index.html");
    }

    @AfterClass
    public static void tearDown( ) throws Exception {
        serverRunner.stopServer();
    }

    @Before
    public void setUp( ) throws Exception {
        connMgr = new BasicHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(connMgr).build();
        indexFile = new File(Constants.SERVER_ROOT_TEST + Constants.INDEX_FILE);
    }

    @Test
    public void testWhenMatchingExistingResource( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(indexFile);
        HttpGet method = new HttpGet(indexUri.toString());

        method.addHeader("If-Match",
                "\"xyzzy\", " + HeaderFormat.formatEtag(resourceHash) + ", \"r2d2xxxx\", \"c3piozzzz\"");
        HttpResponse response = client.execute(method);

        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_OK));
        Header etagHeader = response.getFirstHeader("ETag");
        assertThat(etagHeader.getValue(), equalTo(resourceHash));
    }

    @Test
    public void testWhenNotMatchingExistingResource( ) throws IOException {
        HttpGet method = new HttpGet(indexUri.toString());
        method.addHeader("If-Match", "\"xyzzy\", \"r2d2xxxx\", \"c3piozzzz\"");

        HttpResponse response = client.execute(method);
        
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_PRECONDITION_FAILED));
        String resourceHash = FileUtil.generateStrongValidator(indexFile);
        Header etagHeader = response.getFirstHeader("ETag");
        assertThat(etagHeader.getValue(), equalTo(HeaderFormat.formatEtag(resourceHash)));

    }

}