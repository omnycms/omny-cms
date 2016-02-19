package ca.omny.potent.mappers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.potent.PowerServlet;
import ca.omny.potent.models.ConfigurableProxyRoute;
import ca.omny.potent.models.OmnyRouteConfiguration;
import ca.omny.potent.models.ProxyAndConfiguration;
import ca.omny.request.RequestResponseManager;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import java.util.Collection;
import java.util.Map;

public class OmnyRouteMapper {

    ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    public OmnyRouteConfiguration getConfiguration(IDocumentQuerier querier) {
        return querier.get("omny_routes", OmnyRouteConfiguration.class);
    }

    public static String getProxyRoute(RequestResponseManager request) {
        return request.getRequest().getUri();
    }

    public ProxyAndConfiguration getAppropriateProxy(String url, IDocumentQuerier querier) {
        if (configurationReader.getConfigurationString("OMNY_USE_DYNAMIC_PROXY") != null) {
            Map routes = querier.get("proxy_routes", Map.class);
            RoutingTree<Map> router = new RoutingTree<>("");
            if(routes==null) {
                return null;
            }
            for (Object key : routes.keySet()) {
                ConfigurableProxyRoute route = new ConfigurableProxyRoute(key.toString(), (Map) routes.get(key));
                router.addRoute(route);
            }
            IRoute<Map> match = router.matchPath(url);
            if (match != null) {
                Map matching = match.getObject();
                if (matching != null) {
                    Collection<IOmnyProxyService> proxyServices = PowerServlet.getProxyServices();
                    for(IOmnyProxyService proxy: proxyServices) {
                        if(matching.get("proxyName").equals(proxy.getId())) {
                            ProxyAndConfiguration configuration = new ProxyAndConfiguration();
                            configuration.setConfiguration(matching);
                            configuration.setProxy(proxy);
                            return configuration;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    
}
