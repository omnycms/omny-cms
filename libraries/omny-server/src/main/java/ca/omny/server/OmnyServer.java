package ca.omny.server;

import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import org.eclipse.jetty.server.Server;

public class OmnyServer {
    
    public void createServer(int port, IEnvironmentToolsProvider provider, Collection<OmnyApi> apis) throws Exception {
        Server server = new Server(port);
        OmnyHandler handler = new OmnyHandler(provider, apis);
        server.setHandler(handler);
        
        server.setStopAtShutdown(true);
        
        server.start();
        System.out.println("Server started.");
        server.join();      
    }
}
