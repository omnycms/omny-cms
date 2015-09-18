package ca.omny.extension.proxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IOmnyProxyService {
    void proxyRequest(String hostHeader, String uid, Map configuration, HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException, IOException;
    String getRoutingPattern();
    String getId();
}
