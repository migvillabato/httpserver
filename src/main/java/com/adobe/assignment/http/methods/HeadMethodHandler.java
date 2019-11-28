package com.adobe.assignment.http.methods;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;

/**
 * A HEAD request handler. 
 * 
 * This handler knows how to service HTTP HEAD requests.
 * 
 * It behaves as the GET handler but does not send contents back.
 * E.g. in case of a file request of an existing file
 * in server, the response gives details (metadata) of the file:
 * length in bytes (Content-Length) and type (Content-Type).
 * 
 * @author Miguel Villanueva
 * 
 */
public class HeadMethodHandler extends OnlyReaderMethodHandler implements HttpMethodHandler {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    private ServerConfig config;

    public void init( ServerConfig config ) {
        this.config = config;
    }

    public boolean canHandle( HttpRequest request ) {
        return request.getMethod().equals(HttpConstants.METHOD_HEAD);
    }

    public void handle( HttpRequest request, HttpResponse response ) {
            doHead(request, response);
    }

    private void doHead( HttpRequest request, HttpResponse response ) {
        File file = new File(config.getWebRoot(), request.getRequestURI());

        if (file.exists()) {
            try {
                if (file.isFile()) {
                    setHeadersForFile(file, response);
                    String eTag = getEtag(file, response);
                    log.finest("Head: " + response.getStatus() + " File served with ETag: " + eTag);
                    
                    response.setHeader( HttpConstants.HEADER_ETAG, eTag );
                    
                    response.setHeader( HttpConstants.HEADER_LAST_MODIFIED, 
                            this.getLastModifiedString(file) );
                    
                } else if (file.isDirectory()) {
                    setHeadersForDirList(file, response);
                    log.finest(response.getStatus() + "Head: served a directory list.");
                }
                response.write();
            } catch (IOException e) {
                log.finest("Internal error detected.");
                response.sendError(HttpResponse.SC_INTERNAL_ERROR);
            }
        } else {
            // file does not exist
            response.sendError(HttpResponse.SC_NOT_FOUND);
        }
    }
}
