package ca.omny.services.sites.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.sites.SiteHelper;
import ca.omny.sites.mappers.SiteMapper;
import ca.omny.sites.models.SiteDetails;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class Sites implements OmnyApi {

    Gson gson;
    
    @Inject
    SiteHelper siteHelper;
    
    @Inject
    SiteMapper siteMapper;

    public Sites() {
        gson = new Gson();
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
            SiteDetails site = siteMapper.getSite(siteName);
            if(site==null) {
                return new ApiResponse("", 404);
            }
            return new ApiResponse(site, 200);
        } else {
            return new ApiResponse(siteHelper.getSites(requestResponseManager.getUserId()),200);
        }
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        try {
            SiteDetails site = requestResponseManager.getEntity(SiteDetails.class);
            siteHelper.createSite(site.getSubdomain(),site.getSiteName(),requestResponseManager.getUserId());
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
        siteMapper.deleteSite(requestResponseManager.getRequestHostname());
        return new ApiResponse("", 200);
    }
   
}
