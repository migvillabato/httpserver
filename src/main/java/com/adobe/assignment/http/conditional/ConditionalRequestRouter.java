package com.adobe.assignment.http.conditional;

import java.util.List;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.methods.HttpMethodHandler;
import com.adobe.assignment.http.server.ServerConfig;

public class ConditionalRequestRouter {

    private ServerConfig serverConfig;

    public ConditionalRequestRouter(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void temptConditionalRequest( HttpRequest request, HttpResponse response, HttpMethodHandler handler ) {

        ConditionEvaluatorFactory factory = new ConditionEvaluatorFactory();
        List< ConditionEvaluator > evaluators = factory.getEvaluators(request);

        if(!evaluators.isEmpty()){
            ConditionalHandler conditionalHandler = ConditionalHandlersFactory.getConditionalHandler(handler);
            conditionalHandler.init(serverConfig, evaluators);
            conditionalHandler.handle(request, response, handler);
        } else  // In case there is one handler without conditional requests support.
            handler.handle(request, response);

    }
}
