package com.adobe.assignment.http.utils;

public class UserMessagesUtil {
    public final static String USAGE_MSG = "Correct usage: [Port] [Server root] [Verbose mode] "
            + "\n Listening port (optional int): Server listening port for http requests. "
            + "\n   if not given: assumed value in properties. If not in properties, then '8080'."
            + "\n Server root (optional string): Server root directory."
            + "\n   if not given: assumed value in properties. If not in properties, then './www/' (directory 'www' in jar location)."
            + "\n Debug (optional bool): Turns off/on verbose mode. Values should be in form 'true'/'false'.";
    
    public static void printUsageMessage(){
        System.out.println("\n-----------------------  HTTP 1.1 SERVER ----------------------------------------\n");
        System.out.println(UserMessagesUtil.USAGE_MSG);
        System.out.println("\n---------------------------------------------------------------------------------\n");
    }
}
