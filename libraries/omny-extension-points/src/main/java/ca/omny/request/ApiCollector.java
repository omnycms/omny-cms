package ca.omny.request;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;

public class ApiCollector {
    LinkedList<OmnyApi> allApis;
    public ApiCollector(IOmnyApiRegistrar... apiRegistrars) {
        allApis = new LinkedList<>();
        for(IOmnyApiRegistrar registrar: apiRegistrars) {
            Collection<OmnyApi> apis = registrar.getApis();
            if(apis != null) {
                allApis.addAll(apis);
            }
        }
    }
    
    public Collection<OmnyApi> getApis() {
        return allApis;
    }
}
