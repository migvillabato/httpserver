package com.adobe.assignment.http.methods;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.PreconditionResult;
/**
 * A GET request handler.
 * 
 * This handler knows how to service HTTP GET requests. This implementation
 * simply reads the file present in the request URI from the file system and
 * sends it back to the client. Moreover, if the URI indicates a directory, the
 * server returns the list of contents in it.
 *
 * @author Miguel Villanueva
 * @author Alfusainey Jallow
 * 
 */
public class GetMethodHandler extends OnlyReaderMethodHandler implements HttpMethodHandler {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    private ServerConfig config;

    /**
     * @see HttpMethodHandler#init(ServerConfig, MIMETyper)
     */
    public void init( ServerConfig config ) {
        this.config = config;
    }

    /**
     * @see HttpMethodHandler#handle(HttpRequest, HttpResponse)
     */
    public void handle( HttpRequest request, HttpResponse response ) {
        doGet(request, response);
    }

    /**
     * Determines if this handler can handle the specified HTTP request.
     * 
     * @param request
     *            The request to handle.
     * 
     * @return true if the specified request is a GET request and false
     *         otherwise.
     */
    public boolean canHandle( HttpRequest request ) {
        return request.getMethod().equals(HttpConstants.METHOD_GET);
    }

    protected void doGet( HttpRequest request, HttpResponse response ) {

        File file = new File(config.getWebRoot(), request.getRequestURI());

        PreconditionResult preconds = checkPreconditions(file);

        if (preconds.getValue() == false) {
            response.sendError(preconds.getStatusCode());
            return;
        }

        try {
            if (file.isFile()) {
                setHeadersForFile(file, response);
                byte[] fileToServe = Utils.fileToCharArray(file);
                response.setContent(fileToServe);
                
                // Set Etag.
                String eTag = getEtag(file, response);
                log.finest(response.getStatus() + "Get: File served with ETag: " + eTag);
                
                response.setHeader( HttpConstants.HEADER_ETAG, eTag );
                
                response.setHeader( HttpConstants.HEADER_LAST_MODIFIED, 
                        this.getLastModifiedString(file) );
                
            } else if (file.isDirectory()) {
                response.setContent(setHeadersForDirList(file, response));
                log.finest(response.getStatus() + "Get: Served a directory list.");
            }
            response.write();
        } catch (IOException e) {
            log.finest("Internal error detected.");
            response.sendError(HttpResponse.SC_INTERNAL_ERROR);
        }

    }
}
