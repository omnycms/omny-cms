package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.pages.models.PageModulesUpdate;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.storage.StorageSystem;

public class PageModules implements OmnyApi {
    
    Gson gson;
    PageHelper pageHelper;
    PageMapper pageMapper;
        
    public PageModules() {
        gson = new Gson();
        pageMapper = new PageMapper();
        pageHelper = PageHelper.getDefaultPageHelper();
    }

    @Override
    public String getBasePath() {
        return PageApiConstants.base+"/modules";
    }

    @Override
    public String[] getVersions() {
        return PageApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {       
        PageModulesUpdate pageModulesUpdate = requestResponseManager.getEntity(PageModulesUpdate.class);
        StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
        pageMapper.updateModules(pageModulesUpdate, requestResponseManager.getRequestHostname(), storageSystem);
        return new ApiResponse("", 200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
