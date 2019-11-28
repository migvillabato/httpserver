package com.adobe.assignment.http.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.methods.HttpMethodHandler;
import com.adobe.assignment.http.utils.UserMessagesUtil;

/**
 * This class bundles the information required to bootstrap the web-server.
 * 
 * @author Alfusainey Jallow, University of the Gambia
 */

public class ServerConfig {
    static public final String LOGGER_ID = "socketServer.logger";
    private final static Logger log = Logger.getLogger("socketServer.logger");
    static private final int PORT_NOT_SET = -1;
    static private final String SERVER_ROOT_NOT_SET = "";

    /**
     * Server port read from command arguments.
     */
    private int portArgument = PORT_NOT_SET;

    /**
     * Server root read from command arguments.
     */
    private String serverRootArgument = SERVER_ROOT_NOT_SET;

    /**
     * The configured web server port. If the port number configuration is
     * missing, a default port of zero is selected.
     */
    private static final String PORT = "webserver.port";

    /**
     * The host machine this webserver is running. This server defaults to
     * localhost if no server is configured.
     */
    private static final String HOST = "webserver.host";

    /**
     * The configured root directory of the webserver.
     */
    private static final String WEB_ROOT = "webserver.webroot";

    /**
     * The configured root directory of the webserver.
     */
    private static final String SERVER_TIMEOUT = "webserver.timeout";

    private final Properties props;

    public ServerConfig() {
        props = new Properties();
    }

    public void load( InputStream inputStream ) throws IOException {
        props.load(inputStream);
    }

    public int getPort( ) {
        if (this.portArgument != PORT_NOT_SET) {
            return portArgument;
        } else {
            int port = Integer.parseInt(props.getProperty(PORT, "8080"));
            return port;
        }
    }

    public String getWebRoot( ) {
        if (this.serverRootArgument != SERVER_ROOT_NOT_SET) {
            return this.serverRootArgument;
        } else {
            return props.getProperty(WEB_ROOT, ".");
        }
    }

    public void setPort( int port ) {
        this.portArgument = port;
    }

    public void setServerRoot( String serverRoot ) {
        this.serverRootArgument = serverRoot;
    }

    /**
     * Retrieve the configured host of the HTTP Server.
     * 
     * @return The host the server is running on. Localhost is returned in the
     *         event of a missing host configuration.
     */
    public String getHost( ) {
        return props.getProperty(HOST, "localhost");
    }

    public int getTimeout( ) {
        return Integer.parseInt(props.getProperty(SERVER_TIMEOUT, "15000"));
    }

    public void checkProperties( ) {
        if (this.portArgument == PORT_NOT_SET)
            ServerConfig.checkValidPortArg(props.getProperty(PORT, "8080"));

        if (this.serverRootArgument == SERVER_ROOT_NOT_SET)
            ServerConfig.checkValidServerRoot(props.getProperty(WEB_ROOT, SERVER_ROOT_NOT_SET));
    }

    /**
     * 
     * @param arg
     * @return int
     * @author Miguel Villanueva
     */
    static public int checkValidPortArg( String arg ) {
        int portInt = PORT_NOT_SET;
        try {
            portInt = Integer.parseInt(arg);

            if (portInt > Short.MAX_VALUE || portInt < 0) {
                System.out.println("Port in invalid range. \n");
                System.exit(1);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid port argument. \n");
            System.exit(1);
        }

        return portInt;
    }

    static public String checkValidServerRoot( String serverRootPath ) {
        File serverRoot = new File(serverRootPath);
        if (!serverRoot.exists() || !serverRoot.isDirectory()) {
            System.out.println("Invalid server root: " + serverRootPath);
            System.exit(1);
        }
        return serverRootPath;
    }

}
