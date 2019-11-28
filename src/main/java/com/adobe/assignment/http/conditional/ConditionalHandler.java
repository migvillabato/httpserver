package com.adobe.assignment.http.conditional;

import java.util.List;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.methods.HttpMethodHandler;
import com.adobe.assignment.http.server.ServerConfig;

public interface ConditionalHandler {
    public void init( ServerConfig config, List< ConditionEvaluator > validators );

    public void handle( HttpRequest request, HttpResponse response, HttpMethodHandler handler);

}
