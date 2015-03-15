package ca.omny.all;

import ca.omny.all.configuration.DatabaseConfigurationValues;
import ca.omny.all.configuration.DatabaseConfigurer;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.PowerServlet;
import ca.omny.server.OmnyServer;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class OmnyAllInOneServer {

    public static void main(String[] args) throws Exception {
        
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        if(args.length>1 &&args[0].equals("configure")) {
            //read from JSON file
            String configString = IOUtils.toString(new FileInputStream(new File(args[1])));
            Gson gson = new Gson();
            DatabaseConfigurationValues configurationValues = gson.fromJson(configString, DatabaseConfigurationValues.class);
            DatabaseConfigurer configurer = container.instance().select(DatabaseConfigurer.class).get();
            configurer.configure(configurationValues);
        }
        int edgeRouterPort = 8080;
        if(System.getenv("omny_edge_port")!=null) {
            edgeRouterPort = Integer.parseInt(System.getenv("omny_edge_port"));
        }
        Server edgeServer = new Server(edgeRouterPort);
        PowerServlet edgeServlet = container.instance().select(PowerServlet.class).get();
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        String staticFilesDirectory = ".";
        if(System.getenv("omny_static_location")!=null) {
            staticFilesDirectory = System.getenv("omny_static_location");
        }
        context.addServlet(new ServletHolder(edgeServlet),"/*");
        IDocumentQuerier querier = container.instance().select(IDocumentQuerier.class).get();
        UiHandler uiHandler = new UiHandler(staticFilesDirectory, querier);

        HandlerList handlers = new HandlerList();
        Handler[] handlerList = new Handler[] { uiHandler,context };
        if(System.getenv("omny_no_ui")!=null) {
            handlerList = new Handler[] { context };
        }
        handlers.setHandlers(handlerList);
        edgeServer.setHandler(handlers);
        
        OmnyServer server = new OmnyServer();
        int port = 8077;
        if(System.getenv("omny_all_port")!=null) {
            port = Integer.parseInt(System.getenv("omny_all_port"));
        }
        
        edgeServer.start();
        if(System.getenv("omny_route_dynamic")==null) {
            server.createServer(port);
        } else {
            edgeServer.join();
        }
        edgeServer.stop();
    }
}
