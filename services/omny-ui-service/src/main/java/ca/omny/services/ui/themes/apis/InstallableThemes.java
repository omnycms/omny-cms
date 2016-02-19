package ca.omny.services.ui.themes.apis;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.services.ui.apis.UiApiConstants;
import ca.omny.services.ui.themes.mappers.ThemeMapper;
import ca.omny.storage.GlobalStorage;
import ca.omny.storage.StorageSystem;

public class InstallableThemes implements OmnyApi{
    
    ThemeMapper themeMapper = new ThemeMapper();

    @Override
    public String getBasePath() {
        return UiApiConstants.base+"/themes/installable/{theme}";
    }

    @Override
    public String[] getVersions() {
        return UiApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String theme = requestResponseManager.getPathParameter("theme");
        StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
        GlobalStorage globalStorage = new GlobalStorage(requestResponseManager.getDatabaseQuerier(), storageSystem, requestResponseManager.getStorageDevice(), ConfigurationReader.getDefaultConfigurationReader());
        if(theme==null) {
            return new ApiResponse(themeMapper.getInstallableThemes(globalStorage), 200);
        } else {
            return new ApiResponse(themeMapper.getGlobalTheme(theme,globalStorage), 200);
        }
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
