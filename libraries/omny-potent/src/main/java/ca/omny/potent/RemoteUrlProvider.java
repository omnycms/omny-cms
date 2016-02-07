package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.extension.proxy.IRemoteUrlProvider;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.potent.models.OmnyEndpoint;
import ca.omny.potent.models.OmnyRouteConfiguration;
import ca.omny.request.RequestResponseManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Alternative
public class RemoteUrlProvider implements IRemoteUrlProvider {
    
    ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    IDocumentQuerier db = QuerierFactory.getDefaultQuerier();
    OmnyRouteMapper routeMapper = new OmnyRouteMapper();

    public RemoteUrlProvider() {
        
    }
    
    public RemoteUrlProvider(ConfigurationReader configurationReader, IDocumentQuerier db, OmnyRouteMapper routeMapper) {
        this.configurationReader = configurationReader;
        this.db = db;
        this.routeMapper = routeMapper;
    }

    public String getRemoteUrl(String route, RequestResponseManager req) {  
        String decodedQueryString = req.getRequest().getQueryString();
        try {
            if(decodedQueryString != null) {
                decodedQueryString = URLDecoder.decode(decodedQueryString, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
        }
        if(configurationReader.getSimpleConfigurationString("OMNY_ROUTE_DYNAMIC") != null) {
            String key = db.getKey("omny_hosts","current");
            Type type = new TypeToken<List<RemoteHost>>(){}.getType();
            String value = db.get(key, String.class);
            List<RemoteHost> hosts = new Gson().fromJson(value, type);
            RemoteHost remoteHost = hosts.get((int)(Math.random()*hosts.size()));
            
            return remoteHost.getProtocol()+"://"+remoteHost.getHostname()+":"+remoteHost.getPort()+route+ "?" + decodedQueryString;
        }
        String host = "http://127.0.0.1:8077";
        if (configurationReader.getConfigurationString("OMNY_PROXY_HOST") != null) {
            host = configurationReader.getConfigurationString("OMNY_PROXY_HOST");
        }
        OmnyRouteConfiguration configuration = routeMapper.getConfiguration();
        String remoteRoute = OmnyRouteMapper.getProxyRoute(req);
        OmnyEndpoint bestMatch = this.getBestMatch(route, configuration.getEndpoints());
        if (bestMatch == null) {
            return null;
        }
        
        String remoteUrl = host + bestMatch.getContextRoot() + route + "?" + decodedQueryString;
        
        return remoteUrl;
    }
    
    private OmnyEndpoint getBestMatch(String route, Collection<OmnyEndpoint> endpoints) {
        for (OmnyEndpoint endpoint : endpoints) {
            String pattern = endpoint.getPattern().replace("*", "");
            if (route.startsWith(pattern)) {
                return endpoint;
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return "default";
    }
}
