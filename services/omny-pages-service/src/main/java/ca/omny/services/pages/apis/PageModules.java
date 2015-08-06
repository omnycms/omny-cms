package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.pages.mappers.ThemeMapper;
import ca.omny.pages.models.PageModulesUpdate;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import javax.inject.Inject;

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
        pageMapper.updateModules(pageModulesUpdate, requestResponseManager.getRequestHostname(), requestResponseManager.getStorageSystem());
        return new ApiResponse("", 200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
