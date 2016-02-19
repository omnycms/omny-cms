package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.extension.proxy.IRemoteUrlProvider;
import ca.omny.potent.models.OmnyEndpoint;
import ca.omny.request.RequestResponseManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
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

    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String[] AUTO_HEADERS = {HeaderManager.USER_HEADER, HeaderManager.HOST_HEADER, CONTENT_LENGTH_HEADER, "Transfer-encoding"};

    ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    IRemoteUrlProvider remoteUrlProvider;

    CloseableHttpClient httpclient = HttpClients.createDefault();

    @Override
    public void proxyRequest(RequestResponseManager req, Map<String,String> configuration) throws MalformedURLException, IOException {
        if(remoteUrlProvider==null) {
            remoteUrlProvider = RemoteUrlProviderFactory.getDefaultProvider();
        }
        try {
            String hostHeader = req.getRequestHostname();
            String uid = req.getUserId();
            String remoteUrl = remoteUrlProvider.getRemoteUrl(req.getRequest().getUri(), req);

            if (remoteUrl == null) {
                req.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            URIBuilder uriBuilder = new URIBuilder(remoteUrl);
            String method = req.getRequest().getMethod();
            AbstractHttpMessage message = this.getMessage(method, uriBuilder.build(), req);

            HeaderManager manager = new HeaderManager();
            Map<String, String> sendableHeaders = manager.getSendableHeaders(hostHeader, uid, req);
            for(String header: sendableHeaders.keySet()) {
                message.addHeader(header,sendableHeaders.get(header));
            }
            CloseableHttpResponse response = httpclient.execute((HttpUriRequest) message);
            req.getResponse().setStatus(response.getStatusLine().getStatusCode());
            try {
                HttpEntity entity = response.getEntity();
                Header[] allHeaders = response.getAllHeaders();
                for (Header header : allHeaders) {
                    req.getResponse().setHeader(header.getName(), header.getValue());
                }
                if (entity != null) {
                    req.getResponse().getWriter().print(entity.getContent());                    
                }
            } finally {
                response.close();
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(PassThroughProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String getId() {
        return "PASS_THROUGH";
    }

    public AbstractHttpMessage getMessage(String method, URI uri, RequestResponseManager req) throws IOException {
        switch (method.toLowerCase()) {
            case "get":
                return new HttpGet(uri);
            case "post":
                HttpPost post = new HttpPost(uri);
                post.setEntity(new InputStreamEntity(req.getBody(false)));
                return post;
            case "put":
                HttpPut put = new HttpPut(uri);
                put.setEntity(new InputStreamEntity(req.getBody(false)));
                return put;
            case "delete":
                HttpDelete httpDelete = new HttpDelete(uri);
                return httpDelete;
            case "options":
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
