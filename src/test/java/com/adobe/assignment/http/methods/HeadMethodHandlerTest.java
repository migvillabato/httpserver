package com.adobe.assignment.http.methods;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.testutils.Constants;
import com.adobe.assignment.http.utils.FileUtil;

@RunWith(MockitoJUnitRunner.class)
public class HeadMethodHandlerTest {

    private final File INDEX_FILE = new File(Constants.SERVER_ROOT_TEST, Constants.INDEX_FILE);

    @Mock
    ServerConfig serverConfig;

    @InjectMocks
    HeadMethodHandler headHandler;

    @Mock
    HttpRequest request;

    @Mock
    HttpResponse response;

    @Before
    public void setUp( ) {
        when(request.getMethod()).thenReturn(HttpConstants.METHOD_HEAD);
        when(serverConfig.getWebRoot()).thenReturn(Constants.SERVER_ROOT_TEST);
    }

    @Test
    public void testWhenRequestedResourceIsNonExistingFile( ) {
        when(request.getRequestURI()).thenReturn("notExistingFile.html");

        headHandler.handle(request, response);

        verify(response, times(1)).sendError(HttpResponse.SC_NOT_FOUND);
        verify(response, Mockito.never()).setContent(any(byte[].class));
    }

    @Test
    public void testWhenRequestedResourceIsExistingFile( ) throws IOException {
        when(request.getRequestURI()).thenReturn(INDEX_FILE.getName());

        headHandler.handle(request, response);

        byte[] fileContents = Utils.fileToCharArray(INDEX_FILE);

        // Headers
        MIMETyper mt = MIMETyper.createInstance();
        verify(response, times(1)).setContentLength(eq(INDEX_FILE.length()));
        verify(response, times(1)).setContentType(mt.getContentTypeFor(INDEX_FILE.getName()));
        verify(response, Mockito.never()).setContent(any(byte[].class));

        verify(response, Mockito.never()).setStatus(anyInt());
        verify(response, times(1)).write();
        verify(response, times(1)).setHeader(HttpConstants.HEADER_ETAG, FileUtil.generateStrongValidator(INDEX_FILE));
    }

    @Test
    public void testWhenRequestedResourceIsExistingDirectory( ) {
        when(request.getRequestURI()).thenReturn("dir2/dir2.1");

        headHandler.handle(request, response);

        verify(response, times(1)).setContentType(MIMETyper.DEFAULT);

        verify(response, Mockito.never()).sendError(anyInt());
        verify(response, Mockito.never()).setContent(any(byte[].class));
        verify(response, times(1)).setContentLength(anyInt());
    }

    @Test
    public void testWhenRequestedResourceIsNonExistingDirectory( ) {
        when(request.getRequestURI()).thenReturn("dir2/dir235");

        headHandler.handle(request, response);

        verify(response, times(1)).sendError(HttpResponse.SC_NOT_FOUND);
        verify(response, Mockito.never()).setContent(any(byte[].class));
    }

    @Test
    public void testExtraCaseExistingFile( ) throws IOException {
        when(request.getRequestURI()).thenReturn("dir2/dir2.2/file1.txt");

        headHandler.handle(request, response);

        File targetFile = new File(Constants.SERVER_ROOT_TEST, "dir2/dir2.2/file1.txt");
        byte[] fileContents = Utils.fileToCharArray(targetFile);

        // Headers
        MIMETyper mt = MIMETyper.createInstance();
        verify(response, times(1)).setContentLength(eq(targetFile.length()));
        verify(response, times(1)).setContentType(mt.getContentTypeFor(INDEX_FILE.getName()));
        verify(response, Mockito.never()).setContent(any(byte[].class));

        verify(response, Mockito.never()).setStatus(anyInt());
        verify(response, times(1)).write();
        verify(response, times(1)).setHeader(HttpConstants.HEADER_ETAG, FileUtil.generateStrongValidator(targetFile));
    }

}
