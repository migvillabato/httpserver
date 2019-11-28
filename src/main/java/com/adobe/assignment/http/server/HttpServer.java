package com.adobe.assignment.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
/**
 * A simplified HTTP server
 * 
 * Note: This version has been extended to support any type of HTTP request. The
 * extension mechanism uses 'configuration over code-modification'. To do so,
 * simply configure an implementation of the HttpMethodHandler interface. Such
 * an implementation will concretely specify how such an impl will handle and
 * service http request. Once implemented, add the FQN of the class to the XML
 * configuration file. This file is read once upon bootstraping this HTTP
 * Server.
 * 
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow, University of the Gambia
 * @author Miguel Villanueva.
 * 
 * @version 3.0
 */
public class HttpServer {

    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);
	


	/**
	 * Configuration file for the different HTTP request handlers. Different request
	 * handler implementations can be configured in this XML and read by the 
	 * server during startup.
	 * 
	 * Advantage: Offers configuration over code modification.
	 */
    
	private final ExecutorService threadPool;
	private final ServerSocket serverSocket;

	private ServerConfig config;

	/**
	 * Default COnstructor
	 */
	public HttpServer(ServerConfig config) throws IOException {
	    this.config = config;
	    log.info("Using the following server root: " + config.getWebRoot());
		// Initialize the Server's configuration.	
		serverSocket = new ServerSocket(config.getPort());
		
		log.info("Created Server Socket on " + config.getPort());

		threadPool = Executors.newCachedThreadPool();
		serverSocket.setSoTimeout(100000);
	}



	/**
	 * Stop the threads in the pool
	 */
	public void stopPool() {
		// Prevent new Runnable objects from being submitted
		threadPool.shutdown();

		try {
			// Wait for existing connections to complete
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
				// Stop executing threads
				threadPool.shutdownNow();

				// Wait again
				if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
					log.info("Could not stop thread pool.");
				}
				log.info("Thread pool terminated.");
			}
		} catch (InterruptedException ie) {
			// Stop executing threads
			threadPool.shutdownNow();

			// Propagate the interrupt status --> for shutting down thread-pool.
			Thread.currentThread().interrupt();
		}
        log.info("Thread pool terminated.");
	}

	/**
	 * Start accepting connections from clients.
	 */
	public void start() {
		try {
			HttpConnectionHandler connection;
			Socket socket;

			while (true) {
				try {
                    log.finer("Waiting for http connections:");
					socket = serverSocket.accept();
					connection = new HttpConnectionHandler(socket, config);
                    log.finer("Connection received:");

					// Add the connection to a BlockingQueue<Runnable> object
					// and, ultimately, call it's run() method in a thread
					// in the pool
					threadPool.execute(connection);
				} catch (SocketTimeoutException ste) {
					// do nothing
				} catch (IOException e) {
					log.warning("Fatal error: "+e.getMessage());
				}
			}
		} finally {
			stopPool();
		}
	}
}
