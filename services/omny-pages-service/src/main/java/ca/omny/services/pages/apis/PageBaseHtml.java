package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.storage.StorageSystem;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageBaseHtml implements OmnyApi {
    
    Gson gson;
    
    PageHelper pageHelper;
    
    PageMapper pageMapper;
    
    
    public PageBaseHtml() {
        gson = new Gson();
        pageMapper = new PageMapper();
        pageHelper = PageHelper.getDefaultPageHelper();
    }

    @Override
    public String getBasePath() {
        return PageApiConstants.base+"/basehtml";
    }

    @Override
    public String[] getVersions() {
        return PageApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        try {
            String page = requestResponseManager.getQueryStringParameter("page");
            StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
            return new ApiResponse(pageHelper.getPageBaseHtml(requestResponseManager.getRequestHostname(),page, storageSystem, requestResponseManager.getDatabaseQuerier(),false),200);
        } catch (IOException ex) {
            Logger.getLogger(DetailedPages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ApiResponse("", 500);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {       
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
