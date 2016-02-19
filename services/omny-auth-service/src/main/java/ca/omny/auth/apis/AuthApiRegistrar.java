package ca.omny.auth.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AuthApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new GoogleLogin());
        apis.add(new Permissions());
        apis.add(new Sessions());
        apis.add(new Users());
        return apis;
    }
}
