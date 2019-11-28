package com.adobe.assignment.http.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import com.adobe.assignment.http.server.ServerConfig;

/**
 * This formatter is used by the console logger. The purpose is to produce in
 * this case a less verbose message by skipping the first line of each record,
 * e.g:
 * 
 * (Skiped line:) Jul 16, 2016 3:25:56 AM org.challenge.net.Server$EventListener
 * run some message.
 * 
 * @author Miguel Villanueva
 */
class BriefFormatter extends Formatter {
    public BriefFormatter() {
        super();
    }

    @Override
    public String format( final LogRecord record ) {
        return record.getMessage() + "\n";
    }
}

/**
 * If the flag Debug is true, the logger flushes all messages (no matter what
 * importance level) to console and log file. If the flag Debug is false, the
 * logger flushes to console and log file only messages with importance
 * Level.finest.
 * 
 * @author Miguel Villanueva
 */
public class SimpleLogger {
    private static FileHandler fh = null;
    public static boolean Debug = false;

    public static void init( ) {
        try {
            fh = new FileHandler("HttpServer.log", false);

            Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);

            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new BriefFormatter());

            log.addHandler(handler);

            if (Debug) {
                handler.setLevel(Level.ALL);
                log.setLevel(Level.ALL);
            } else {
                handler.setLevel(Level.FINER);
                log.setLevel(Level.FINEST);
            }

            log.fine("Logging system running...");

        } catch (IOException e) {
            System.out.println("Failed to open log file. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error initializing the logger. " + e.getMessage());
        }

    }

}