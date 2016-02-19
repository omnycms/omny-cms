package ca.omny.services.menus.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MenuApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new Menus());
        apis.add(new MenuEntries());
        return apis;
    }
}
