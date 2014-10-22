package ca.omny.themes.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.themes.mappers.ThemeMapper;
import ca.omny.themes.models.ThemeInstallRequest;
import java.util.Collection;
import javax.inject.Inject;

public class Themes implements OmnyApi {
    
    @Inject
    ThemeMapper themeMapper;
    
    @Override
    public String getBasePath() {
        return ThemeApiConstants.base+"/{theme}";
    }

    @Override
    public String[] getVersions() {
        return ThemeApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        Collection<String> themes = themeMapper.getThemes(requestResponseManager.getRequestHostname());
        return new ApiResponse(themes, 200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        ThemeInstallRequest themeInstallRequest = requestResponseManager.getEntity(ThemeInstallRequest.class);
        String theme = requestResponseManager.getPathParameter("theme");
        themeMapper.copyGlobalTheme(themeInstallRequest.getFromTheme(), requestResponseManager.getRequestHostname(), theme);
        return new ApiResponse("", 200);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
