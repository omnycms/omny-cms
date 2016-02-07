package ca.omny.themes.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.themes.mappers.ThemeMapper;
import ca.omny.themes.models.Theme;
import javax.inject.Inject;

public class DefaultTheme implements OmnyApi{
    
    @Inject
    ThemeMapper themeMapper;

    @Override
    public String getBasePath() {
        return ThemeApiConstants.base+"/default";
    }

    @Override
    public String[] getVersions() {
        return ThemeApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String defaultTheme = themeMapper.getDefaultTheme(requestResponseManager.getRequestHostname(), requestResponseManager.getDatabaseQuerier());
        return new ApiResponse(defaultTheme, 200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        Theme theme = requestResponseManager.getEntity(Theme.class);
        themeMapper.setDefaultTheme(requestResponseManager.getRequestHostname(), theme.getName(), requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 200);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
