package ca.omny.extension.proxy;

import ca.omny.request.RequestResponseManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface IOmnyProxyService {
    void proxyRequest(RequestResponseManager requestResponseManager, Map<String, String> configuration) throws MalformedURLException, IOException;
    String getRoutingPattern();
    String getId();
}
