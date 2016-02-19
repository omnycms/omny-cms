package ca.omny.services.sites.apis.registration;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import ca.omny.services.sites.apis.Health;
import ca.omny.services.sites.apis.Robots;
import ca.omny.services.sites.apis.SiteMaps;
import ca.omny.services.sites.apis.Sites;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SiteApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new Robots());
        apis.add(new Sites());
        apis.add(new SiteMaps());
        return apis;
    }
   
}
