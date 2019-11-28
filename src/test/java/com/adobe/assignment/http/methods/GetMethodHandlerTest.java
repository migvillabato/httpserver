package com.adobe.assignment.http.methods;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.FileUtil;

/**
 * Tests resource retreivement using GET method.
 * 
 * @author Miguel Villanueva.
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class GetMethodHandlerTest {

    // static private final String SERVER_ROOT_TEST = "serverTestData/root/";
    // static private final String INDEX_FILE = "index.html";
    private final File INDEX_FILE = new File(Constants.SERVER_ROOT_TEST, Constants.INDEX_FILE);
    
    @Mock
    ServerConfig serverConfig;

    @InjectMocks
    GetMethodHandler getHandler;

    @Mock
    HttpRequest request;

    @Mock
    HttpResponse response;

    @Before
    public void setUp( ) {
        when(request.getMethod()).thenReturn(HttpConstants.METHOD_GET);
        when(serverConfig.getWebRoot()).thenReturn(Constants.SERVER_ROOT_TEST);
    }

    @Test
    public void testWhenRequestedResourceIsNonExistingFile( ) {
        when(request.getRequestURI()).thenReturn("notExistingFile.html");

        getHandler.handle(request, response);
        
        verify(response, times(1)).sendError(HttpResponse.SC_NOT_FOUND);
        verify(response, Mockito.never()).setContent(any(byte[].class));
    }

    @Test
    public void testWhenRequestedResourceIsExistingFile( ) throws IOException {
        when(request.getRequestURI()).thenReturn(INDEX_FILE.getName());
        
        getHandler.handle(request, response);

        byte[] fileContents = Utils.fileToCharArray(INDEX_FILE);

        // Headers
        MIMETyper mt = MIMETyper.createInstance();
        verify(response).setContentLength(eq(INDEX_FILE.length()));
        verify(response).setContentType(mt.getContentTypeFor(INDEX_FILE.getName()));
        verify(response).setContent(eq(fileContents));

        verify(response, Mockito.never()).setStatus(anyInt());
        verify(response, times(1)).write();
        verify(response, times(1)).setHeader( HttpConstants.HEADER_ETAG, FileUtil.generateStrongValidator(INDEX_FILE));
    }

    @Test
    public void testWhenRequestedResourceIsExistingDirectory( ) {
        when(request.getRequestURI()).thenReturn("dir2/dir2.1");
        
        getHandler.handle(request, response);

        verify(response, times(1)).setContentType(MIMETyper.DEFAULT);

        verify(response, Mockito.never()).sendError(anyInt());
        verify(response, times(1)).setContent(any(byte[].class));
        verify(response, times(1)).setContentLength(anyInt());

    }
    
    @Test
    public void testWhenRequestedResourceIsNonExistingDirectory( ) {
        when(request.getRequestURI()).thenReturn("dir2/dir235");
        
        getHandler.handle(request, response);

        verify(response, times(1)).sendError(HttpResponse.SC_NOT_FOUND);
        verify(response, Mockito.never()).setContent(any(byte[].class));
    }
    
    @Test
    public void testExtraCaseExistingFile( ) throws IOException {
        when(request.getRequestURI()).thenReturn("dir2/dir2.2/file1.txt");
        
        getHandler.handle(request, response);

        File targetFile = new File(Constants.SERVER_ROOT_TEST, "dir2/dir2.2/file1.txt");
        byte[] fileContents = Utils.fileToCharArray(targetFile);

        // Headers
        MIMETyper mt = MIMETyper.createInstance();
        verify(response).setContentLength(eq(targetFile.length()));
        verify(response).setContentType(mt.getContentTypeFor(INDEX_FILE.getName()));
        verify(response).setContent(eq(fileContents));

        verify(response, Mockito.never()).setStatus(anyInt());
        verify(response, times(1)).write();
        verify(response, times(1)).setHeader( HttpConstants.HEADER_ETAG, FileUtil.generateStrongValidator(targetFile));
    }
    
}
