package ca.omny.potent.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.models.OmnyRouteConfiguration;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class OmnyRouteMapper {
    @Inject
    IDocumentQuerier querier;
    
    public OmnyRouteConfiguration getConfiguration() {
        return querier.get("omny_routes", OmnyRouteConfiguration.class);
    }
    
    public static String getProxyRoute(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}
