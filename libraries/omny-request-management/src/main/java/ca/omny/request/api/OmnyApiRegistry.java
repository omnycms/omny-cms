package ca.omny.request.api;

import java.util.Collection;
import java.util.LinkedList;

public class OmnyApiRegistry {
    static Collection<OmnyApi> registeredApis = new LinkedList<>();
    
    public static void registerApi(OmnyApi api) {
        registeredApis.add(api);
    }

    public static Collection<OmnyApi> getRegisteredApis() {
        return registeredApis;
    }
}
