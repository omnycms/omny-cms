package ca.omny.services.sites.apis;

import ca.omny.request.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new Robots());
        OmnyApiRegistry.registerApi(new SiteMaps());
        OmnyApiRegistry.registerApi(new Sites());
    }
}
