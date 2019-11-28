package com.adobe.assignment.http.conditional;

import java.util.ArrayList;
import java.util.List;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;

/**
 * This factory creates all necessary evaluators according to request headers.
 * They are ordered in a list respecting the evaluation precedence specified in 
 * RFC 7232.
 * 
 * @author Miguel
 *
 */
public class ConditionEvaluatorFactory {
    public List<ConditionEvaluator> getEvaluators( HttpRequest request )
    {
        List<ConditionEvaluator> validators = new ArrayList<ConditionEvaluator>();
        
        if(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH) != null)
            validators.add(new IfMatchEvaluator() );
        
        // This two evaluators exclude each other as RFC 7232 indicates.
        if(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH) != null)
            validators.add(new IfNoneMatchEvaluator() );
        else if(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE) != null)
            validators.add(new IfModifiedSinceEvaluator());
                   
        return validators;
    }
}
