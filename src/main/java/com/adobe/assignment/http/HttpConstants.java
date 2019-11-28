package com.adobe.assignment.http;

public interface HttpConstants {

    // --------------------------------< Headers (Names and Value Constants)
    // >---
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_PERSISTENT_CONNECTION = "Connection";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_VALUE_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";

    // --------------------------------< Methods constants >---
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_HEAD = "HEAD";

    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";

    /*
     * Date format pattern used to parse HTTP date headers in RFC 1123 format.
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1036 format.
     */
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in ANSI C
     * <code>asctime()</code> format.
     */
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    public static final String[] DEFAULT_PATTERNS = new String[] { 
         PATTERN_ASCTIME, 
         PATTERN_RFC1036,
         PATTERN_RFC1123
    };
    
}
