package com.adobe.assignment.http.conditional;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.methods.GetMethodHandler;
import com.adobe.assignment.http.methods.HttpMethodHandler;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.PreconditionResult;

public class ConditionalGetHeadHandler implements ConditionalHandler {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    private ServerConfig serverConfig;
    private List< ConditionEvaluator > evaluators;

    public void handle( HttpRequest request, HttpResponse response, HttpMethodHandler handler ) {

        PreconditionResult preconds = new PreconditionResult();

        File file = new File(serverConfig.getWebRoot(), request.getRequestURI());
        preconds = GetMethodHandler.checkPreconditions(file);
        
        // RFC 7232: A server MUST ignore all received preconditions if its response to the same request
        // without those conditions would have been a status code other than a 2xx (Successful) or 412 (Precondition Failed)
        // Then if file not found, sends correspondent error.
        if (preconds.getValue() == false) {
            response.sendError(preconds.getStatusCode());
            log.finest("Conditional " + handler.getClass().getName() + "on non existing resource."
                    + " sent status code" + preconds.getStatusCode());
            return;
        }
        
        try {
            for (ConditionEvaluator evaluator : this.evaluators) {

                preconds = evaluator.validate(request, response, serverConfig);

                if (preconds.getValue() == false) {
                    response.setStatus(preconds.getStatusCode());
                    response.write();
                    log.finest(response.getStatus() + " " + evaluator.getClass().getName() + " precondition failed.");
                    return;
                }
                
                log.finest(evaluator.getClass().getName() + " precondition fulfiled.");
            }

            // Executes GET/HEAD method.
            handler.handle(request, response);

        } catch (ParseException e) {
            // e.g. If-Match header value has an unrecognized pattern.
            response.sendError(HttpResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            response.sendError(HttpResponse.SC_INTERNAL_ERROR);
        }

    }

    public void init( ServerConfig config, List< ConditionEvaluator > evaluators ) {
        this.serverConfig = config;
        this.evaluators = evaluators;
    }

    // public boolean canHandle( HttpRequest request ) {
    // if(request.getMethod().equals(HttpConstants.METHOD_GET)
    // || request.getMethod().equals(HttpConstants.METHOD_HEAD))
    // return true;
    // else
    // return false;
    // }

}
