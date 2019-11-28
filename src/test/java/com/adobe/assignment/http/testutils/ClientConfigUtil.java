package com.adobe.assignment.http.testutils;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ClientConfigUtil {
    static public PoolingHttpClientConnectionManager createMultiThreadedHttpConnectionManager( ) {
        PoolingHttpClientConnectionManager conMgr = new PoolingHttpClientConnectionManager();

        HttpHost localhost = new HttpHost(Constants.DEFAULT_HOST, Constants.DEFAULT_PORT);
        HttpRoute route = new HttpRoute(localhost);

        conMgr.setMaxTotal(Constants.MAX_TOTAL_CONN);
        conMgr.setMaxPerRoute(route, Constants.MAX_CONN_PER_HOST); 
        
        conMgr.setConnectionConfig( localhost , ConnectionConfig.DEFAULT);
        conMgr.setSocketConfig(localhost, SocketConfig.DEFAULT);
        return conMgr;
    }
    
    static public URI createDefaultUriWithPath(String path) throws URISyntaxException{
        URIBuilder uri = new URIBuilder();
        uri.setPort(Constants.DEFAULT_PORT);
        uri.setHost(Constants.DEFAULT_HOST);
        uri.setPath(path);
        uri.setScheme("http");
        return uri.build();
    }


}
