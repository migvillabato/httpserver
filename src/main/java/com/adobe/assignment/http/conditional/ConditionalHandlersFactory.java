package com.adobe.assignment.http.conditional;

import com.adobe.assignment.http.methods.GetMethodHandler;
import com.adobe.assignment.http.methods.HeadMethodHandler;
import com.adobe.assignment.http.methods.HttpMethodHandler;

public class ConditionalHandlersFactory {

    static public ConditionalHandler getConditionalHandler( HttpMethodHandler handler ) {
        if (handler instanceof GetMethodHandler
                || handler instanceof HeadMethodHandler )
            return new ConditionalGetHeadHandler();
        else
            return null;
    }

}
