package ca.omny.potent.mappers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.potent.PowerServlet;
import ca.omny.potent.models.ConfigurableProxyRoute;
import ca.omny.potent.models.OmnyRouteConfiguration;
import ca.omny.potent.models.ProxyAndConfiguration;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class OmnyRouteMapper {

    IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
    ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    public OmnyRouteConfiguration getConfiguration() {
        return querier.get("omny_routes", OmnyRouteConfiguration.class);
    }

    public static String getProxyRoute(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public ProxyAndConfiguration getAppropriateProxy(String url) {
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
