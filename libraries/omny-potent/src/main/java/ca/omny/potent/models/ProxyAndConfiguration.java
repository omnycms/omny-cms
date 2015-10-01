package ca.omny.potent.models;

import ca.omny.extension.proxy.IOmnyProxyService;
import java.util.Map;

public class ProxyAndConfiguration {
    IOmnyProxyService proxy;
    Map configuration;

    public IOmnyProxyService getProxy() {
        return proxy;
    }

    public void setProxy(IOmnyProxyService proxy) {
        this.proxy = proxy;
    }

    public Map getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map configuration) {
        this.configuration = configuration;
    }
    
}
