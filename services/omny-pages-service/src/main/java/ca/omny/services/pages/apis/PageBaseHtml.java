package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.pages.models.PageModulesUpdate;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class PageBaseHtml implements OmnyApi {
    
    Gson gson;
    
    @Inject
    PageHelper pageHelper;
    
    @Inject
    PageMapper pageMapper;
    
    @Inject
    RequestResponseManager requestResponseManager;
    
    public PageBaseHtml() {
        gson = new Gson();
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
            
            return new ApiResponse(pageHelper.getPageBaseHtml(requestResponseManager.getRequestHostname(),page,false),200);
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
