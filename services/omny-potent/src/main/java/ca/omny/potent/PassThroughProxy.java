package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.potent.models.OmnyEndpoint;
import ca.omny.potent.models.OmnyRouteConfiguration;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class PassThroughProxy {

    public static final String USER_HEADER = "X-UserId";
    public static final String HOST_HEADER = "X-Origin";

    @Inject
    OmnyRouteMapper routeMapper;

    @Inject
    IDocumentQuerier querier;

    @Inject
    ConfigurationReader configurationReader;

    public void proxyRequest(String hostHeader, String uid, HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException, IOException {
        OmnyRouteConfiguration configuration = routeMapper.getConfiguration();
        String route = OmnyRouteMapper.getProxyRoute(req);
        OmnyEndpoint bestMatch = this.getBestMatch(route, configuration.getEndpoints());
        if (bestMatch == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String host = "http://127.0.0.1:8077";
        if (configurationReader.getConfigurationString("proxyHost") != null) {
            host = configurationReader.getConfigurationString("proxyHost");
        }
        String remoteUrl = host + bestMatch.getContextRoot() + route + "?" + req.getQueryString();
        URL url = new URL(remoteUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(req.getMethod());

        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toString();
            if (!headerName.equals(USER_HEADER) && !headerName.equals(HOST_HEADER)) {
                String headerValue = req.getHeader(headerName);
                connection.addRequestProperty(headerName, headerValue);
            }
        }
        connection.addRequestProperty(HOST_HEADER, hostHeader);
        connection.addRequestProperty(USER_HEADER, uid);
        if (!req.getMethod().toLowerCase().equals("get")) {
            connection.setDoOutput(true);
            IOUtils.copy(req.getInputStream(), connection.getOutputStream());
        }
        try {
            IOUtils.copy(connection.getInputStream(), resp.getOutputStream());
        } catch (IOException e) {
        }
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (String header : headerFields.keySet()) {
            if (header != null) {
                resp.addHeader(header, headerFields.get(header).get(0));
            }
        }
        resp.setStatus(connection.getResponseCode());
    }
    
    public String getSimpleOrigin(String origin) {
        if (origin.contains("://")) {
            origin = origin.substring(origin.indexOf("://") + 3);
        }
        if (origin.contains(":")) {
            origin = origin.substring(0, origin.indexOf(":"));
        }
        return origin;
    }

    public OmnyEndpoint getBestMatch(String route, Collection<OmnyEndpoint> endpoints) {
        for (OmnyEndpoint endpoint : endpoints) {
            String pattern = endpoint.getPattern().replace("*", "");
            if (route.startsWith(pattern)) {
                return endpoint;
            }
        }
        return null;
    }
}
