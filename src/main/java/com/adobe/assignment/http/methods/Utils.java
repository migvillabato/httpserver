package com.adobe.assignment.http.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;



public class Utils {
    static public byte[] fileToCharArray( File file ) throws IOException {
        // Exception handling.
        FileInputStream fis = new FileInputStream(file);
        return IOUtils.toByteArray(fis);
    }
    
    static public String printDirListHtml(String list)
    {
        return "<HTML><BODY>Directory list: <br>" + list
                + "</BODY></HTML>";
    }
}
