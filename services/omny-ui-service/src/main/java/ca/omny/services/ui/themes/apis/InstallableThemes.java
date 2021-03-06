package ca.omny.services.ui.themes.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.services.ui.apis.UiApiConstants;
import ca.omny.services.ui.themes.mappers.ThemeMapper;
import ca.omny.storage.IStorage;
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
        IStorage storage = requestResponseManager.getStorageDevice();
        if(theme==null) {
            return new ApiResponse(themeMapper.getInstallableThemes(storage), 200);
        }
        return new ApiResponse("", 404);
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
