package ca.omny.services.pages.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PageApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new PageModules());
        apis.add(new Pages());
        apis.add(new PageBaseHtml());
        apis.add(new DetailedPages());
        return apis;
    }
}
