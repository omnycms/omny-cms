package ca.omny.themes.apis;

import ca.omny.request.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new DefaultTheme());
        OmnyApiRegistry.registerApi(new Health());
        OmnyApiRegistry.registerApi(new InstallableThemes());
        OmnyApiRegistry.registerApi(new Samples());
        OmnyApiRegistry.registerApi(new Themes());
    }
}
