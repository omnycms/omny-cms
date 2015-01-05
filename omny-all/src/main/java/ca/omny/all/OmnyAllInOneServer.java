package ca.omny.all;

import ca.omny.potent.PowerServlet;
import ca.omny.server.OmnyServer;
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
        
        UiHandler uiHandler = new UiHandler(staticFilesDirectory);

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
        server.createServer(port);
        edgeServer.stop();
    }
}
