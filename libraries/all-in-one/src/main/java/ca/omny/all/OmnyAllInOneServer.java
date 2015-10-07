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
        if (configurationReader.getConfigurationString("OMNY_EDGE_PORT") != null) {
            edgeRouterPort = Integer.parseInt(configurationReader.getSimpleConfigurationString("OMNY_EDGE_PORT"));
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
        if (configurationReader.getConfigurationString("OMNY_STATIC_LOCATION") != null) {
            staticFilesDirectory = configurationReader.getSimpleConfigurationString("OMNY_STATIC_LOCATION");
        }
        context.addServlet(new ServletHolder(edgeServlet), "/*");
        IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
        UiHandler uiHandler = new UiHandler(staticFilesDirectory, querier);

        HandlerList handlers = new HandlerList();
        Handler[] handlerList = new Handler[]{uiHandler, context};
        if (configurationReader.getConfigurationString("OMNY_NO_UI") != null) {
            handlerList = new Handler[]{context};
        }
        handlers.setHandlers(handlerList);
        edgeServer.setHandler(handlers);

        
        int port = 8077;
        if (configurationReader.getConfigurationString("OMNY_ALL_PORT") != null) {
            port = Integer.parseInt(configurationReader.getSimpleConfigurationString("OMNY_ALL_PORT"));
        }

        edgeServer.start();
        if (configurationReader.getConfigurationString("OMNY_ROUTE_DYNAMIC") == null) {
            server.createServer(port);
        } else {
            edgeServer.join();
        }
        edgeServer.stop();
    }
}
