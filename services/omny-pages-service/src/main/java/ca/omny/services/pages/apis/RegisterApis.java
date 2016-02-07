package ca.omny.services.pages.apis;

import ca.omny.request.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new DetailedPages());
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new PageBaseHtml());
        OmnyApiRegistry.registerApi(new PageModules());
        OmnyApiRegistry.registerApi(new Pages());
    }
}
