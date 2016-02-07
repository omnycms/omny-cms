package ca.omny.services.menus.apis;

import ca.omny.request.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new MenuEntries());
        OmnyApiRegistry.registerApi(new Menus());
    }
}
