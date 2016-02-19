package ca.omny.services.extensibility.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExtensibilityApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new DevUiModules());
        apis.add(new InstalledUiModules());
        apis.add(new UiModules());
        apis.add(new DefaultModuleVersion());
        return apis;
    }
}
