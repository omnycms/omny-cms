package ca.omny.potent.models;

import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.routing.IRoute;

public class ProxyRoute implements IRoute<IOmnyProxyService>{

    private final IOmnyProxyService proxyService;
    
    public ProxyRoute(IOmnyProxyService proxyService) {
        this.proxyService = proxyService;
    }
    
    @Override
    public String getPath() {
        return proxyService.getRoutingPattern();
    }

    @Override
    public IOmnyProxyService getObject() {
        return proxyService;
    }
    
}
