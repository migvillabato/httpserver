package com.adobe.assignment.http.methods;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.server.ServerConfig;

public class MethodHandlerFactory {
    static public HttpMethodHandler createMethodHandler( HttpRequest request, ServerConfig config )
    {       
        if(request.getMethod().equals(HttpConstants.METHOD_GET)){
            HttpMethodHandler h =  new GetMethodHandler();
            h.init(config);
            return h;
        }else if(request.getMethod().equals(HttpConstants.METHOD_HEAD)){
            HttpMethodHandler h =  new HeadMethodHandler();
            h.init(config);
            return h;
        }else
            return null;
    }
}
