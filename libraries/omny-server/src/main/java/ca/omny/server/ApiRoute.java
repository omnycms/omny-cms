package ca.omny.server;

import ca.omny.request.OmnyApi;
import ca.omny.routing.IRoute;

public class ApiRoute implements IRoute<OmnyApi> {

    final OmnyApi api;
    final String version;

    public ApiRoute(OmnyApi api, String version) {
        this.api = api;
        this.version = version;
    }

    @Override
    public String getPath() {
        return "/api/v"+version+api.getBasePath();
    }

    @Override
    public OmnyApi getObject() {
        return api;
    }
    
}
