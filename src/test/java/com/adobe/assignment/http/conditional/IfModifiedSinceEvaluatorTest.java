package com.adobe.assignment.http.conditional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Calendar;
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
import com.adobe.assignment.http.testutils.FileUtils;
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.PreconditionResult;

@RunWith(MockitoJUnitRunner.class)
public class IfModifiedSinceEvaluatorTest {

    @Mock
    HttpRequest request;
    
    @Mock
    HttpResponse response;

    @Mock
    ServerConfig serverConfig;
    
    // set some reference point 100 seconds before current moment.
    Date someInstant = Calendar.getInstance().getTime();


    @Before
    public void setUp( ) {
        when(request.getRequestURI()).thenReturn(Constants.LAST_MODIFIED);
        when(serverConfig.getWebRoot()).thenReturn(Constants.SERVER_ROOT_TEST);
    }

    @Test
    public void testWhenDatesAreEqualThenSendsStatusNotModified( ) throws Exception {
        FileUtils.setLastModifiedAttr(Constants.SERVER_ROOT_TEST +
                Constants.LAST_MODIFIED, someInstant.getTime());

        String someInstantStr = DatesUtils.formatHttpDate(someInstant);
        when(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE))
            .thenReturn(someInstantStr);

        IfModifiedSinceEvaluator validator = new IfModifiedSinceEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_MODIFIED));

    }        
    
    @Test
    public void testWhenResourceIsAlreadyUpdatedInClient( ) throws Exception {
        Date someInstantBefore = new Date(someInstant.getTime() - 15000);
        FileUtils.setLastModifiedAttr(Constants.SERVER_ROOT_TEST +
                Constants.LAST_MODIFIED, someInstantBefore.getTime());

        when(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE))
            .thenReturn(DatesUtils.formatHttpDate(someInstant));

        IfModifiedSinceEvaluator validator = new IfModifiedSinceEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(false));
        assertThat(preconds.getStatusCode(), is(HttpResponse.SC_NOT_MODIFIED));
        String someInstanceBeforeStr = DatesUtils.formatHttpDate(someInstantBefore);

    }
    
    @Test
    public void testWhenResourceIsNotUpdatedInClient( ) throws Exception {
        FileUtils.setLastModifiedAttr(Constants.SERVER_ROOT_TEST +
                Constants.LAST_MODIFIED, someInstant.getTime());

        Date someInstantBefore = new Date(someInstant.getTime() - 15000);
        when(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE))
            .thenReturn(DatesUtils.formatHttpDate(someInstantBefore));

        IfModifiedSinceEvaluator validator = new IfModifiedSinceEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
        verify(response, times(0)).setHeader(anyString(), anyString());
    }

    @Test
    public void testWhenDateCannotBeParsedThenHeaderOmitted( ) throws Exception {
        Date someInstantBefore = new Date(someInstant.getTime() - 15000);
        FileUtils.setLastModifiedAttr(Constants.SERVER_ROOT_TEST +
                Constants.LAST_MODIFIED, someInstantBefore.getTime());

        when(request.getHeaderValue(HttpConstants.HEADER_IF_MODIFIED_SINCE))
            .thenReturn("16 de abril de 1989"); // E.g. a date in Spanish is not recognized.

        IfModifiedSinceEvaluator validator = new IfModifiedSinceEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }
    
    
    @Test
    public void testWhenDirectoryRequestedThenPreconditionIsTrue( ) throws Exception {
        // We try to retrieve a non existing resource with a conditional method.
        when(request.getRequestURI()).thenReturn("dir1");

        IfModifiedSinceEvaluator validator = new IfModifiedSinceEvaluator();
        PreconditionResult preconds = validator.validate(request, response, serverConfig);

        assertThat(preconds.getValue(), is(true));
    }
}
