package ca.omny.themes.apis;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.storage.GlobalStorage;
import ca.omny.storage.StorageSystem;
import ca.omny.themes.mappers.ThemeMapper;
import ca.omny.themes.models.ThemeInstallRequest;
import java.util.Collection;

public class Themes implements OmnyApi {

    ThemeMapper themeMapper;
    
    public Themes() {
        themeMapper = new ThemeMapper();
    }
    
    @Override
    public String getBasePath() {
        return ThemeApiConstants.base+"/{theme}";
    }

    @Override
    public String[] getVersions() {
        return ThemeApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
        Collection<String> themes = themeMapper.getThemes(requestResponseManager.getRequestHostname(), storageSystem);
        return new ApiResponse(themes, 200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        ThemeInstallRequest themeInstallRequest = requestResponseManager.getEntity(ThemeInstallRequest.class);
        String theme = requestResponseManager.getPathParameter("theme");
        StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
        GlobalStorage globalStorage = new GlobalStorage(requestResponseManager.getDatabaseQuerier(), storageSystem, requestResponseManager.getStorageDevice(), ConfigurationReader.getDefaultConfigurationReader());
        themeMapper.copyGlobalTheme(themeInstallRequest.getFromTheme(), requestResponseManager.getRequestHostname(), theme, globalStorage);
        return new ApiResponse("", 200);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
