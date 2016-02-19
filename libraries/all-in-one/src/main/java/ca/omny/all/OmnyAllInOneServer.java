package ca.omny.all;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.db.IDocumentQuerier;
import ca.omny.potent.PowerServlet;
import ca.omny.request.OmnyApi;
import ca.omny.server.OmnyClassRegister;
import ca.omny.server.OmnyServer;
import java.util.Collection;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class OmnyAllInOneServer {

    private static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    OmnyServer server;
    Server edgeServer;
    IEnvironmentToolsProvider toolsProvider;
    Collection<OmnyApi> apis;
    
    public OmnyAllInOneServer(IEnvironmentToolsProvider toolsProvider, Collection<OmnyApi> apis) {
        this.toolsProvider = toolsProvider;
        this.apis = apis;
        server = new OmnyServer();
        int edgeRouterPort = 8080;
        if (configurationReader.getConfigurationString("OMNY_EDGE_PORT") != null) {
            edgeRouterPort = Integer.parseInt(configurationReader.getSimpleConfigurationString("OMNY_EDGE_PORT"));
        }
        edgeServer = new Server(edgeRouterPort);
    }

    public void start() throws Exception {
        OmnyClassRegister classRegister = new OmnyClassRegister();
        classRegister.loadFromEnvironment();

        PowerServlet edgeServlet = new PowerServlet(toolsProvider);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        String staticFilesDirectory = ".";
        if (configurationReader.getConfigurationString("OMNY_STATIC_LOCATION") != null) {
            staticFilesDirectory = configurationReader.getSimpleConfigurationString("OMNY_STATIC_LOCATION");
        }
        context.addServlet(new ServletHolder(edgeServlet), "/*");
        IDocumentQuerier querier = toolsProvider.getDefaultDocumentQuerier();
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
            if(configurationReader.getConfigurationString("OMNY_BIND_PORT")==null) {
                PowerServlet.addProxyService(new OmnyServiceProxy(toolsProvider, apis));
                edgeServer.join();
            } else {
                server.createServer(port, toolsProvider, apis);
            }
        } else {
            edgeServer.join();
        }
        edgeServer.stop();
    }
}
