package ca.omny.services.extensibility.apis;

import ca.omny.request.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new DefaultModuleVersion());
        OmnyApiRegistry.registerApi(new DevUiModules());
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new InstalledUiModules());
        OmnyApiRegistry.registerApi(new UiModules());
    }
}
