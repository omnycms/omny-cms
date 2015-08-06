package ca.omny.content.apis;

import ca.omny.request.api.OmnyApiRegistry;

public class RegisterApis {
    static {
        OmnyApiRegistry.registerApi(new ContentLocation());
        OmnyApiRegistry.registerApi(new Health());
    }
}
