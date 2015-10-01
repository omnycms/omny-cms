package ca.omny.all;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.potent.PowerServlet;
import ca.omny.server.OmnyHandler;
import ca.omny.server.OmnyServer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class OmnyAllInOneServer {

    private static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    OmnyServer server;
    Server edgeServer;
    
    public OmnyAllInOneServer() {
        server = new OmnyServer();
        int edgeRouterPort = 8080;
        if (System.getenv("omny_edge_port") != null) {
            edgeRouterPort = Integer.parseInt(System.getenv("omny_edge_port"));
        }
        edgeServer = new Server(edgeRouterPort);
    }

    public void start() throws Exception {
        configurationReader.setKey("OMNY_NO_INJECTION", "true");
        OmnyHandler handler = new OmnyHandler();

        
        PowerServlet edgeServlet = new PowerServlet();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        String staticFilesDirectory = ".";
        if (System.getenv("omny_static_location") != null) {
            staticFilesDirectory = System.getenv("omny_static_location");
        }
        context.addServlet(new ServletHolder(edgeServlet), "/*");
        IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
        UiHandler uiHandler = new UiHandler(staticFilesDirectory, querier);

        HandlerList handlers = new HandlerList();
        Handler[] handlerList = new Handler[]{uiHandler, context};
        if (System.getenv("omny_no_ui") != null) {
            handlerList = new Handler[]{context};
        }
        handlers.setHandlers(handlerList);
        edgeServer.setHandler(handlers);

        
        int port = 8077;
        if (System.getenv("omny_all_port") != null) {
            port = Integer.parseInt(System.getenv("omny_all_port"));
        }

        edgeServer.start();
        if (System.getenv("omny_route_dynamic") == null) {
            server.createServer(port);
        } else {
            edgeServer.join();
        }
        edgeServer.stop();
    }
}
