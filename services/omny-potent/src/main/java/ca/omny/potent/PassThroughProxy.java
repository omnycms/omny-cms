package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.extension.proxy.IRemoteUrlProvider;
import ca.omny.potent.models.OmnyEndpoint;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;

public class PassThroughProxy implements IOmnyProxyService {

    public static final String USER_HEADER = "X-UserId";
    public static final String HOST_HEADER = "X-Origin";

    @Inject
    IDocumentQuerier querier;

    @Inject
    ConfigurationReader configurationReader;
    
    @Inject
    IRemoteUrlProvider remoteUrlProvider;

    CloseableHttpClient httpclient = HttpClients.createDefault();
    
    @Override
    public void proxyRequest(String hostHeader, String uid, HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException, IOException {
        try {
            String remoteUrl = remoteUrlProvider.getRemoteUrl(req.getRequestURI(), req);
            
            if(remoteUrl==null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            URIBuilder uriBuilder = new URIBuilder(remoteUrl);
            String method = req.getMethod();
            AbstractHttpMessage message = this.getMessage(method, uriBuilder.build(), req);
            
            Enumeration headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement().toString();
                if (!headerName.equals(USER_HEADER) && !headerName.equals(HOST_HEADER)) {
                    String headerValue = req.getHeader(headerName);
                    message.addHeader(headerName, headerValue);
                }
            }
            message.addHeader(HOST_HEADER, hostHeader);
            message.addHeader(USER_HEADER, uid);
            CloseableHttpResponse response = httpclient.execute((HttpUriRequest)message);
            resp.setStatus(response.getStatusLine().getStatusCode());
            try {
                HttpEntity entity = response.getEntity();
                Header[] allHeaders = response.getAllHeaders();
                for(Header header: allHeaders) {
                    resp.addHeader(header.getName(), header.getValue());
                }
                if(entity!=null) {
                    IOUtils.copy(entity.getContent(), resp.getOutputStream());
                }
            } finally {
                response.close();
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(PassThroughProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AbstractHttpMessage getMessage(String method, URI uri, HttpServletRequest req) throws IOException {
        if(method.toLowerCase().equals("get")) {
            return new HttpGet(uri);
        } else if(method.toLowerCase().equals("post")) {
             HttpPost post = new HttpPost(uri);
             post.setEntity(new InputStreamEntity(req.getInputStream()));
             return post;
        } else if(method.toLowerCase().equals("put")) {
            HttpPut put = new HttpPut(uri);
            put.setEntity(new InputStreamEntity(req.getInputStream()));
            return put;
        } else if(method.toLowerCase().equals("delete")) {
            HttpDelete httpDelete = new HttpDelete(uri);
            return httpDelete;
        }  else if(method.toLowerCase().equals("options")) {
            HttpOptions httpOptions = new HttpOptions(uri);
            return httpOptions;
        }
        return null;
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

    @Override
    public String getRoutingPattern() {
        return "/*";
    }
}
