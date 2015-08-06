package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailedPages implements OmnyApi {
    
    Gson gson;
    PageHelper pageHelper;
    PageMapper pageMapper;
    
    public DetailedPages() {
        gson = new Gson();
        pageHelper = PageHelper.getDefaultPageHelper();
        pageMapper = new PageMapper();
    }

    @Override
    public String getBasePath() {
        return PageApiConstants.base+"/detailed";
    }

    @Override
    public String[] getVersions() {
        return PageApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        try {
            String page = requestResponseManager.getQueryStringParameter("page");
            return new ApiResponse(pageHelper.getPageDetails(requestResponseManager.getRequestHostname(),page,requestResponseManager.getStorageSystem(),requestResponseManager.getDatabaseQuerier(),false),200);
        } catch (IOException ex) {
            Logger.getLogger(DetailedPages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ApiResponse("", 500);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
