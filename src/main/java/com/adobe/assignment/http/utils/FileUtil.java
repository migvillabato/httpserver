package com.adobe.assignment.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

public class FileUtil {
    static public String generateStrongValidator( File file ) throws IOException {
        // At this point was already verified that file exists and is not a
        // directory.
        InputStream fileIStream = new FileInputStream(file);
        String hash = DigestUtils.md5Hex(fileIStream);

        return hash;
    }
}
