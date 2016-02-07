package ca.omny.themes.apis;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.storage.GlobalStorage;
import ca.omny.storage.StorageSystem;
import ca.omny.themes.mappers.ThemeMapper;
import javax.inject.Inject;

public class InstallableThemes implements OmnyApi{
    
    @Inject
    ThemeMapper themeMapper;

    @Override
    public String getBasePath() {
        return ThemeApiConstants.base+"/installable/{theme}";
    }

    @Override
    public String[] getVersions() {
        return ThemeApiConstants.versions;
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
