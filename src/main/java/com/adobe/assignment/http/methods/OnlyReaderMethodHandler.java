package com.adobe.assignment.http.methods;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.server.ServerConfig;
import com.adobe.assignment.http.utils.DatesUtils;
import com.adobe.assignment.http.utils.FileUtil;
import com.adobe.assignment.http.utils.PreconditionResult;

public abstract class OnlyReaderMethodHandler implements HttpMethodHandler {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    static public PreconditionResult checkPreconditions( File file ) {
        PreconditionResult result = new PreconditionResult();
        if (!file.exists()) {
            result.setValue(false);
            result.setStatusCode(HttpResponse.SC_NOT_FOUND);
        }
        return result;
    }

    protected  byte[] setHeadersForDirList( File directory, HttpResponse response ) {
        String[] dirList = directory.list();

        StringBuffer strBuffer = new StringBuffer();
        for (String dirContent : dirList) {
            strBuffer.append(dirContent);
            strBuffer.append("<br>");
        }

        String dirListHtml = Utils.printDirListHtml(strBuffer.toString());
        byte[] dirListBytes = dirListHtml.getBytes();

        response.setContentType(MIMETyper.DEFAULT);
        response.setContentLength(dirListBytes.length);
        return dirListBytes;
    }

    protected void setHeadersForFile( File file, HttpResponse response ) {
        long size = file.length();

        MIMETyper mt = MIMETyper.createInstance();
        log.finer("Serving file " + file.getName() + " " + mt.getContentTypeFor(file.getName()));

        response.setContentType(mt.getContentTypeFor(file.getName()));
        response.setContentLength(size);
    }
    
    protected String getEtag(File file, HttpResponse response) throws IOException{
        return FileUtil.generateStrongValidator(file);
    }
    
    protected String getLastModifiedString(File file) throws IOException{
        Date d = new Date();
        //d.setSeconds(file.lastModified());
        return DatesUtils.formatHttpDate(new Date(file.lastModified()));
    }
}
