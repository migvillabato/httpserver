package com.adobe.assignment.http.testutils;

import com.adobe.assignment.http.server.ServerInit;

public class ServerRunner extends ServerInit implements Runnable {
    Thread t;
    private static final String[] ARGS = { Integer.toString(Constants.DEFAULT_PORT),
            Constants.SERVER_ROOT_TEST,
            "true" };

    public ServerRunner() {
        t = new Thread(this, "Thread: server running.");
        t.start();
    }

    public void run( ) {
        ServerInit.main(ARGS);
    }
    
    public void stopServer(){
        server.stopPool();
    }
    
}
