package ca.omny.server;

import org.eclipse.jetty.server.Server;

public class OmnyServer {
    
    public void createServer(int port) throws Exception {
        Server server = new Server(port);
        OmnyHandler handler = new OmnyHandler();
        server.setHandler(handler);
        
        server.setStopAtShutdown(true);
        
        server.start();
        System.out.println("Server started. Press enter to stop");
        System.in.read();
        server.stop();       
    }
}
