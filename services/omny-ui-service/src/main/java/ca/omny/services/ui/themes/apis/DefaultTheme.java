package ca.omny.services.ui.themes.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.services.ui.apis.UiApiConstants;
import ca.omny.services.ui.themes.mappers.Theme;
import ca.omny.services.ui.themes.mappers.ThemeMapper;

public class DefaultTheme implements OmnyApi{
    
    ThemeMapper themeMapper = new ThemeMapper();

    @Override
    public String getBasePath() {
        return UiApiConstants.base+"/themes/default";
    }

    @Override
    public String[] getVersions() {
        return UiApiConstants.versions;
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
