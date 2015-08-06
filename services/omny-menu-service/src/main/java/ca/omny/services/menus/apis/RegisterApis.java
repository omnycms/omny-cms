package ca.omny.services.menus.apis;

import ca.omny.request.api.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new MenuEntries());
        OmnyApiRegistry.registerApi(new Menus());
    }
}
