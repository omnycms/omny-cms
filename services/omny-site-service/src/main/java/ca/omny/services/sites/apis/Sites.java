package ca.omny.services.sites.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.sites.SiteHelper;
import ca.omny.sites.mappers.SiteMapper;
import ca.omny.sites.models.SiteDetails;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sites implements OmnyApi {

    Gson gson;
    
    SiteHelper siteHelper;
    
    SiteMapper siteMapper;

    public Sites() {
        gson = new Gson();
        siteMapper = new SiteMapper();
        siteHelper = new SiteHelper();
    }

    @Override
    public String getBasePath() {
        return SiteApiConstants.base+"/{site}";
    }

    @Override
    public String[] getVersions() {
        return SiteApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String siteName = requestResponseManager.getPathParameter("site");
        String userId;
        if(siteName!=null) {
            SiteDetails site = siteMapper.getSite(siteName, requestResponseManager.getDatabaseQuerier());
            if(site==null) {
                return new ApiResponse("", 404);
            }
            return new ApiResponse(site, 200);
        } else {
            return new ApiResponse(siteHelper.getSites(requestResponseManager.getUserId(), requestResponseManager.getDatabaseQuerier()),200);
        }
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        try {
            SiteDetails site = requestResponseManager.getEntity(SiteDetails.class);
            siteHelper.createSite(site.getSubdomain(),site.getSiteName(),requestResponseManager.getUserId(), requestResponseManager.getDatabaseQuerier());
            return new ApiResponse("", 201);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Sites.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiResponse("",500);
        }
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        siteMapper.deleteSite(requestResponseManager.getRequestHostname(), requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 200);
    }
   
}
