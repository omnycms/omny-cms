package ca.omny.themes.apis;

import ca.omny.request.IOmnyApiRegistrar;
import ca.omny.request.OmnyApi;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ThemeApiRegistrar implements IOmnyApiRegistrar{

    @Override
    public Collection<OmnyApi> getApis() {
        List<OmnyApi> apis = new LinkedList<>();
        apis.add(new Health());
        apis.add(new DefaultTheme());
        apis.add(new InstallableThemes());
        apis.add(new Samples());
        apis.add(new Themes());
        return apis;
    }
}
