package ca.omny.all;

import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.request.RequestResponseManager;
import ca.omny.server.OmnyHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public class OmnyServiceProxy implements IOmnyProxyService {

    OmnyHandler handler;
    
    public OmnyServiceProxy() {
        handler = new OmnyHandler();
    }
    
    @Override
    public void proxyRequest(RequestResponseManager requestResponseManager, Map<String,String> configuration) throws MalformedURLException, IOException {
        handler.handle(requestResponseManager);
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
