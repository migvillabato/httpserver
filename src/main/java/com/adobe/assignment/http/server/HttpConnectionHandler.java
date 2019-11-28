package com.adobe.assignment.http.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpInputStream;
import com.adobe.assignment.http.HttpOutputStream;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.conditional.ConditionalRequestRouter;
import com.adobe.assignment.http.methods.HttpMethodHandler;
import com.adobe.assignment.http.methods.MethodHandlerFactory;

/**
 * Handle an HTTP 1.0 connection in a new thread of execution
 * 
 * This version:
 * 
 * Addition: I have added logic for providing pluggable support for handling
 * Http requests. This has the advantage that the HttpServer be able to service
 * any type of HTTP requests simply by means of configuration. This adheres to
 * the 'configuration over code modification' design principle, since different
 * Http request handlers can be configured to work with the HTTP server.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow, University of the Gambia
 * 
 * @version 0.3
 */
class HttpConnectionHandler implements Runnable {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    private Socket socket;

    private final ServerConfig config;

    /**
     * The HTTP Request object for this connection.
     */
    private HttpRequest httpRequest;

    /**
     * The HTTP response object for this connection.
     */
    private HttpResponse httpResponse;

    private ConditionalRequestRouter conditionalRequestRouter;

    /**
     * Explicit Value Constructor (Starts the thread of execution)
     * 
     * @param s
     *            The TCP socket for the connection
     * @param The
     *            Server's configuration
     */
    public HttpConnectionHandler(Socket s, ServerConfig config) {
        this.config = config;
        socket = s;

        this.conditionalRequestRouter = new ConditionalRequestRouter(this.config);
    }

    /**
     * The entry point for the thread
     */
    public void run( ) {

        String method;
        log.finest("Dispatching a request.");

        try {

            // Create an empty request and response
            this.httpRequest = new HttpRequest(this.getInputStream());
            this.httpResponse = new HttpResponse(this.getOutputStream());
            boolean keepAlive = true;

            this.socket.setSoTimeout(this.config.getTimeout());
            //this.socket.setKeepAlive(true);

            try {
                while (keepAlive) {

                    // Read and parse the request information
                    httpRequest.read();

                    this.setTimeOutHeader();

                    keepAlive = isResponsePersistent();

                    // Determine the method to use
                    method = httpRequest.getMethod();

                    if( httpRequest.getRequestLine() == null){
                        return; // Very rare case when some clients avoid read-block at inputStream.
                                // therefore not supporting persistent connections.
                    }else if ((httpRequest == null) || (method == null)) {
                        httpResponse.sendError(HttpResponse.SC_BAD_REQUEST);
                        log.finest("Bad request!");
                    } else {
                        
                        //Respond to the request
                        handle(httpRequest, httpResponse);
                        
                    }

                    // Resets request and response.
                    if (keepAlive) {
                        this.httpRequest = new HttpRequest(this.getInputStream());//.clear();
                        this.httpResponse = new HttpResponse(this.getOutputStream());//.clear();
                    }
                }
            } catch (IOException e) {
                log.finest("Timed out reading input stream.");
            }
        } catch (IOException e) {
            log.warning("fail to get request and response. The server should close the socket.");
        } catch (Exception e) {
            httpResponse.sendError(HttpResponse.SC_INTERNAL_ERROR);
            log.warning("Internal error while processing the request.");
        } finally {
            // from Miguel: Closing output stream closes the associated socket.
            // That's why this action
            // must be delayed until the very end if persistent connections are
            // to be supported.
            if (this.httpResponse != null) {
                this.httpResponse.closeOutputStream();
            }
            if (!this.socket.isClosed())
                close();
        }
    }

    public HttpRequest getRequest( ) {
        return httpRequest;
    }

    public HttpResponse getResponse( ) throws IOException {
        if (httpResponse == null) {
            httpResponse = new HttpResponse(getOutputStream());
        }
        return httpResponse;
    }

    private HttpInputStream getInputStream( ) throws IOException {
        return new HttpInputStream(socket.getInputStream());
    }

    private HttpOutputStream getOutputStream( ) throws IOException {
        return new HttpOutputStream(socket.getOutputStream());
    }

    /**
     * Handles the HTTP request. Handling the request involves consulting a list
     * of HttpMethodHandler(s) that knows how to service/deal with the specified
     * request.
     * 
     * If there exist a handler in the list that understands the request, then
     * the handling of the request is delegated to that handler.
     * 
     * @param request
     *            Contents of the request
     * @param response
     *            Used to generate the response
     */
    private void handle( HttpRequest request, HttpResponse response ) {
        HttpMethodHandler handler = MethodHandlerFactory.createMethodHandler(request, config);

        if (handler != null) {
            conditionalRequestRouter.temptConditionalRequest(request, response, handler);
        } else { // else: HTTP method is not supported by this server.
            response.sendError(HttpResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void setTimeOutHeader( ) {
        this.httpResponse.setHeader(HttpConstants.HEADER_PERSISTENT_CONNECTION, HttpConstants.HEADER_VALUE_KEEP_ALIVE);
        this.httpResponse.setHeader(HttpConstants.HEADER_KEEP_ALIVE, "timeout=" + config.getTimeout());
    }

    /**
     * Checks whether this HttpConnection should be kept alive i.e left open. If
     * the request present with this connection has its keep-alive header set,
     * this method returns true and false otherwise.
     * 
     * Note: This method is not used anywhere yet. It would be needed if the
     * server should support persistent connections.
     * 
     * @return
     * @throws IOException
     */
    public boolean isResponsePersistent( ) {
        String keepAlive = getRequest().getHeaderValue("Connection");
        return (keepAlive == null) ? false : keepAlive.equalsIgnoreCase(HttpConstants.HEADER_VALUE_KEEP_ALIVE);
    }

    /**
     * Closes the connection.
     * 
     * @throws IOException
     */
    public void close( ) {
        try {
            socket.close();
        } catch (IOException e) {
            log.warning("Error while closing the socket connection");
        }
    }
    
}
