package com.adobe.assignment.http.conditional;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.PreconditionResult;

public interface ConditionEvaluator {
    public PreconditionResult validate( HttpRequest request, HttpResponse response, ServerConfig config ) throws Exception;
}



