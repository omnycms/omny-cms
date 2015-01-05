package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.potent.models.OmnyEndpoint;
import ca.omny.potent.models.OmnyRouteConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoteUrlProvider {
    
    @Inject
    ConfigurationReader configurationReader;
    
    @Inject
    IDocumentQuerier db;
    
    @Inject
    OmnyRouteMapper routeMapper;
    
    public String getRemoteUrl(String route, HttpServletRequest req) {  
        if(configurationReader.getSimpleConfigurationString("omny_route_dynamic") != null) {
            String key = db.getKey("omny_hosts","current");
            Type type = new TypeToken<List<RemoteHost>>(){}.getType();
            String value = db.get(key, String.class);
            List<RemoteHost> hosts = new Gson().fromJson(value, type);
            RemoteHost remoteHost = hosts.get((int)(Math.random()*hosts.size()));
            return remoteHost.getProtocol()+"://"+remoteHost.getHostname()+":"+remoteHost.getPort()+route+ "?" + req.getQueryString();
        }
        String host = "http://127.0.0.1:8077";
        if (configurationReader.getConfigurationString("proxyHost") != null) {
            host = configurationReader.getConfigurationString("proxyHost");
        }
        OmnyRouteConfiguration configuration = routeMapper.getConfiguration();
        String remoteRoute = OmnyRouteMapper.getProxyRoute(req);
        OmnyEndpoint bestMatch = this.getBestMatch(route, configuration.getEndpoints());
        if (bestMatch == null) {
            return null;
        }
        
        String remoteUrl = host + bestMatch.getContextRoot() + route + "?" + req.getQueryString();
        
        return remoteUrl;
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
