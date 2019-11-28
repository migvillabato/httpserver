package com.adobe.assignment.http.conditional;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.FileUtil;
import com.adobe.assignment.http.utils.HeaderFormat;
import com.adobe.assignment.http.utils.PreconditionResult;

public class IfMatchEvaluator implements ConditionEvaluator {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    @Override
    public PreconditionResult validate( HttpRequest request, HttpResponse response, ServerConfig config )
            throws IOException {
        PreconditionResult preconds = new PreconditionResult();
        File file = new File(config.getWebRoot(), request.getRequestURI());
        if(file.isDirectory())
            return preconds; // preconditions are omitted: execute GET/HEAD handle.

        String fileHash = FileUtil.generateStrongValidator(file);

        List< String > matchersList;
        try {
            matchersList = ConditionalUtils
                    .parseIfMatchHeaderValues(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH));

            if (!matchersList.contains("*") && !matchersList.contains(fileHash)) {
                preconds.setValue(false);
                preconds.setStatusCode(HttpResponse.SC_PRECONDITION_FAILED);
                
                response.setHeader(HttpConstants.HEADER_ETAG, HeaderFormat.formatEtag(fileHash));
                //response.setContentLength(file.length());
            }
        } catch (ParseException e) {
            // If the If-Match header is malformed, then this header is omitted.
            // Setting the current precondition to true has the desired effect,
            // as the handle will be executed as this precondition would not
            // exist.
            log.finest("If-Match header value could not be parsed. Omitting precondition...");
        }

        return preconds;
    }

}
