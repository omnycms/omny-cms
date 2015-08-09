package ca.omny.potent.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.potent.models.OmnyRouteConfiguration;
import javax.servlet.http.HttpServletRequest;

public class OmnyRouteMapper {
    IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
    
    public OmnyRouteConfiguration getConfiguration() {
        return querier.get("omny_routes", OmnyRouteConfiguration.class);
    }
    
    public static String getProxyRoute(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}
