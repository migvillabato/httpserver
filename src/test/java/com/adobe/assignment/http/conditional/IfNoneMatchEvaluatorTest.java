package com.adobe.assignment.http.conditional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.File;

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
import com.adobe.assignment.http.utils.FileUtil;
import com.adobe.assignment.http.utils.HeaderFormat;
import com.adobe.assignment.http.utils.PreconditionResult;

@RunWith(MockitoJUnitRunner.class)
public class IfNoneMatchEvaluatorTest {

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

        when(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH))
                .thenReturn("\"someOtherValidator\", " + HeaderFormat.formatEtag(resourceHash));

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_MODIFIED));
        verify(response, times(1)).setHeader(eq(HttpConstants.HEADER_ETAG), eq(HeaderFormat.formatEtag(resourceHash)));
    }

    @Test
    public void testWhenNoHeaderValidatorMatchesAgainstResourceValidator( ) throws Exception {
        when(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH)).thenReturn("\"someRandomValidator\"");

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    @Test
    public void testWhenHeaderValidatorIsWildcardThenPreconditionFails( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(resourceInServer);

        when(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH)).thenReturn("*");

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_MODIFIED));
        verify(response, times(1)).setHeader(eq(HttpConstants.HEADER_ETAG), eq(HeaderFormat.formatEtag(resourceHash)));
    }

    @Test
    public void testWhenHeaderGeneratesParseErrorThenHeaderIsOmitted( ) throws Exception {
        when(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH)).thenReturn("invalidValidatorWithNoQuotes");

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

    @Test
    public void testWhenHeaderContainsMatchingValidatorInList( ) throws Exception {
        String resourceHash = FileUtil.generateStrongValidator(resourceInServer);

        String validators = "\"validator1\", " + "\"validator2\", " + HeaderFormat.formatEtag(resourceHash);
        when(request.getHeaderValue(HttpConstants.HEADER_IF_NONE_MATCH)).thenReturn(validators);

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_MODIFIED));
        verify(response, times(1)).setHeader(eq(HttpConstants.HEADER_ETAG), eq(HeaderFormat.formatEtag(resourceHash)));
    }

//    @Test
//    public void testConditionalHandlerPreconditionsNotFullfiled( ) throws Exception {
//        // We try to retrieve a non existing resource with a conditional method.
//        when(request.getRequestURI()).thenReturn("NonExistingResource");
//        when(request.getHeaderValue(HttpConstants.HEADER_IF_MATCH)).thenReturn("\"someRandomValidator\"");
//
//        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
//        PreconditionResult preconds = validator.validate(request, response, serverConfig);
//
//        assertThat(preconds.getValue(), is(false));
//        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_FOUND));
//        verify(response, Mockito.never()).setHeader(anyString(), anyString());
//    }
    
    @Test
    public void testWhenDirectoryRequestedThenPreconditionIsTrue( ) throws Exception {
        // We try to retrieve a non existing resource with a conditional method.
        when(request.getRequestURI()).thenReturn("dir1");

        IfNoneMatchEvaluator validator = new IfNoneMatchEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }

}
