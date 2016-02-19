package ca.omny.services.ui.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.services.ui.mappers.PageMapper;
import java.util.Map;

public class Page implements OmnyApi {

    @Override
    public String getBasePath() {
        return UiApiConstants.base+"/pages";
    }

    @Override
    public String[] getVersions() {
        return UiApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String page = requestResponseManager.getQueryStringParameter("page");
        String content = new PageMapper().getPageContent(requestResponseManager.getRequestHostname(), page, requestResponseManager.getStorageDevice(),requestResponseManager.getDatabaseQuerier());
        return new ApiResponse(content, 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        Map<String,String> sections = requestResponseManager.getEntity(Map.class);
        new PageMapper().writeSections(requestResponseManager.getRequestHostname(), requestResponseManager.getQueryStringParameter("page"), sections, requestResponseManager.getStorageDevice());
        return new ApiResponse("", 204);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
