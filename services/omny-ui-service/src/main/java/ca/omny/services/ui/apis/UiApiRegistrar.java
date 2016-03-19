package ca.omny.services.ui.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import ca.omny.services.ui.themes.apis.Templates;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UiApiRegistrar implements IOmnyApiRegistrar {

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new Page());
        apis.add(new Templates());
        return apis;
    }
    
}
