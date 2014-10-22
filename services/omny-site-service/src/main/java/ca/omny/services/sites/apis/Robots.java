package ca.omny.services.sites.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.service.client.PermissionChecker;
import com.google.gson.Gson;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public class Robots implements OmnyApi {

    Gson gson;
    
    @Inject
    PermissionChecker permissionChecker;

    public Robots() {
        gson = new Gson();
    }

    @Override
    public String getBasePath() {
        return SiteApiConstants.base+"/robots";
    }

    @Override
    public String[] getVersions() {
        return SiteApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("User-agent: *\n");
        sb.append("Disallow:\n\n");
        String domain = requestResponseManager.getRequestHostname();
        if(!domain.contains("\\.")) {
            if(domain.equals("www")) {
                domain += ".omny.ca";
            } else {
                domain += ".omny.me";
            }
        }
        String protocol = requestResponseManager.getRequest().isSecure()?"https":"http";
        sb.append(String.format("Sitemap: %s://%s/sitemap.xml",protocol,domain));
        return new ApiResponse(sb.toString(),200);
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
