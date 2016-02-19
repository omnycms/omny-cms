package ca.omny.all;

import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.request.OmnyApi;
import ca.omny.request.OmnyBaseHandler;
import ca.omny.request.RequestResponseManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

public class OmnyServiceProxy implements IOmnyProxyService {

    OmnyBaseHandler handler;

    public OmnyServiceProxy(IEnvironmentToolsProvider provider, Collection<OmnyApi> apis) {
        handler = new OmnyBaseHandler(provider, apis);
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
