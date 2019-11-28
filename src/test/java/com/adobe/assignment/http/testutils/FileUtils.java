package com.adobe.assignment.http.testutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class FileUtils {

    static public void setLastModifiedAttr( String fileName, long milliseconds) throws IOException {
        Path p1 = Paths.get(fileName);
        FileTime fileTime = FileTime.fromMillis(milliseconds);

        Files.setAttribute(p1, "basic:lastModifiedTime", fileTime, LinkOption.NOFOLLOW_LINKS);
    }
    
    static public long getLastModifiedAttr( String fileName) throws Exception {
        File file = new File(fileName);
        return file.lastModified();
    }
}
