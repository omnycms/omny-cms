package ca.omny.all;

import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.server.OmnyHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OmnyServiceProxy implements IOmnyProxyService {

    OmnyHandler handler;
    
    public OmnyServiceProxy() {
        handler = new OmnyHandler();
    }
    
    @Override
    public void proxyRequest(String hostHeader, String uid, Map configuration, HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException, IOException {
        try {
            handler.handle(null, null, req, resp);
        } catch (ServletException ex) {
            Logger.getLogger(OmnyServiceProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getRoutingPattern() {
        return "/*";
    }

    @Override
    public String getId() {
        return "OMNY_ALL_APIS";
    }
    
}
