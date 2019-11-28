package com.adobe.assignment.http.conditional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.testutils.Constants;
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.FileUtil;
import com.adobe.assignment.http.utils.HeaderFormat;
import com.adobe.assignment.http.utils.PreconditionResult;

@RunWith(MockitoJUnitRunner.class)
public class IfMatchEvaluatorTest {

    private final File resourceInServer = new File(Constants.SERVER_ROOT_TEST, Constants.INDEX_FILE);

    @Mock
    HttpRequest request;

    @Mock
    HttpResponse response;

    @Mock
    ServerConfig serverConfig;

    @Before
    public void setUp( ) {
        when(request.getRequestURI()).thenReturn(Constants.INDEX_FILE);
        when(serverConfig.getWebRoot()).thenReturn(Constants.SERVER_ROOT_TEST);
    }

    @Test
    public void testWhenSomeHeaderValidatorMatchesAgainstResourceValidator( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(resourceInServer);

        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn(HeaderFormat.formatEtag(resourceHash));

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    @Test
    public void testWhenNoHeaderValidatorMatchesAgainstResourceValidator( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(resourceInServer);

        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn("\"someRandomValidator\"");

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_PRECONDITION_FAILED));
        verify(response, times(1)).setHeader(eq(HttpConstants.HEADER_ETAG), eq(HeaderFormat.formatEtag(resourceHash)));
    }

    @Test
    public void testWhenHeaderValidatorIsWildcardThenMatchesAlways( ) throws Exception {
        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn("*");

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    @Test
    public void testWhenHeaderGeneratesParseErrorThenHeaderIsOmitted( ) throws Exception {
        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn("invalidValidatorWithNoQuotes");

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    @Test
    public void testWhenHeaderContainsMatchingValidatorInList( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(resourceInServer);

        String validators = "\"validator1\", " + "\"validator2\"" + resourceHash;
        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn(validators);

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    
    @Test
    public void testWhenDirectoryRequestedThenPreconditionIsTrue( ) throws Exception {
        // We try to retrieve a non existing resource with a conditional method.
        when(request.getRequestURI()).thenReturn("dir1");

        IfMatchEvaluator validator = new IfMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }
}
