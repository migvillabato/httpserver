package com.adobe.assignment.http.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.adobe.assignment.http.log.SimpleLogger;
import com.adobe.assignment.http.utils.UserMessagesUtil;

/**
 * Entry point class that reads main arguments to set the server listening port
 * and server root directory. Arguments are optional. In case they were not given,
 * defined properties or its correspondent defaults would be assumed.
 *   Default value for property 'ServerRoot' is '.'
 *   Default value for property 'Port' is '8080'
 *   
 * @author Miguel
 */
public class ServerInit {
    private final static Logger log = Logger.getLogger("socketServer.logger");

    private static final String WEB_SERVER_PROPERTIES = "webserver.properties";
    
    protected static HttpServer server;

    /**
     * The entry point of the application
     * 
     * @param args
     *            The command line arguments
     */
    public static void main( String[] args ) {
        UserMessagesUtil.printUsageMessage();

        ServerConfig config = new ServerConfig();
       
        if (args.length > 0)
            config.setPort(ServerConfig.checkValidPortArg(args[0]));
        if (args.length > 1)
            config.setServerRoot(ServerConfig.checkValidServerRoot(args[1]));
        if (args.length > 2 && Boolean.parseBoolean(args[2]) == true)
            SimpleLogger.Debug = true;

        readProperties(config);
        config.checkProperties();
        
        try {
            // Construct and start the server
            SimpleLogger.init();

            server = new HttpServer(config);
            server.start();

        } catch (IOException e) {
            log.warning("Failed to initialize server.");
        } finally {
            if (server != null) {
                server.stopPool();
            }
        }
    }

    /**
     * Initializes the HTTP server. The initialization includes reading the
     * configuration needed to bootstrap the HTTP Server. This includes the
     * properties and XML file configurations.
     */
    static private void readProperties( ServerConfig config) {
        // Load both configuration as a resource.
        InputStream propsStream = HttpServer.class.getClassLoader().getResourceAsStream(WEB_SERVER_PROPERTIES);
        if (propsStream != null) {
            try {
                config.load(propsStream);
            } catch (IOException e) {
                log.warning("fatal error " + e.getMessage());
            }
        } else {
            log.finest(
                    "Fail to locate mandatory configuration files for this web-server. A default host and port will be used!");
        }
    }


}
