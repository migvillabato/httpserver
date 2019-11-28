package com.adobe.assignment.http.utils;

public class HeaderFormat {
    static public String formatEtag( String string )
    {
        return '"' + string + '"';
    }
}
