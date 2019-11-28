package com.adobe.assignment.http.conditional;

import java.io.File;
import java.text.ParseException;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.PreconditionResult;

/**
 * 
 * Validates that
 * 
 * @author Miguel
 */
public class IfModifiedSinceEvaluator implements ConditionEvaluator {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    @Override
    public PreconditionResult validate( HttpRequest request, HttpResponse response, ServerConfig config ) {
        PreconditionResult preconds = new PreconditionResult();
        File file = new File(config.getWebRoot(), request.getRequestURI());
        if (file.isDirectory())
            return preconds; // preconditions are omitted: execute GET/HEAD
                             // handle.

        long lastModifiedServer = file.lastModified();
        long lastModifiedClient;

        try {
            lastModifiedClient = DatesUtils
                    .parseHttpDate(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE));
            if (lastModifiedServer - lastModifiedClient < 1000) { // modified client got last resource representation?
                preconds.setValue(false);
                
                // As RFC 7232 indicates: a 304 response should include any of following headers:
                // Cache-Control, Content-Location, Date,ETag, Expires, and Vary and NO BODY.
                // I'm including the ETag.
                preconds.setStatusCode(HttpResponse.SC_NOT_MODIFIED);
            }
        } catch (ParseException e) {
            // If the If-Modified-Since header is malformed, then this header is
            // omitted.
            // Setting the current precondition to true has the desired effect.
            // as the handle will be executed as this precondition would not
            // exist.
            log.finest("If-Match header value could not be parsed. Omitting precondition...");
        }

        return preconds;
    }

}
