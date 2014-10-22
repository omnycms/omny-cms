package ca.omny.services.pages.apis;

import com.google.gson.Gson;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.helpers.PageHelper;
import ca.omny.pages.models.CreatePageRequest;
import ca.omny.pages.models.PageDetailsUpdate;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import javax.inject.Inject;

public class Pages implements OmnyApi {
    
    Gson gson;
    
    @Inject
    PageHelper pageHelper;
    
    @Inject
    PageMapper pageMapper;
    
    @Inject
    RequestResponseManager requestResponseManager;
    
    public Pages() {
        gson = new Gson();
    }

    @Override
    public String getBasePath() {
        return PageApiConstants.base+"/{page}";
    }

    @Override
    public String[] getVersions() {
        return PageApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String path = requestResponseManager.getQueryStringParameter("path");
        if(path==null) {
            path = "pages/current";
        }
        return new ApiResponse(pageHelper.getPages(path, requestResponseManager.getRequestHostname()),200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        CreatePageRequest createPageRequest = requestResponseManager.getEntity(CreatePageRequest.class);
        pageMapper.createPage(createPageRequest, requestResponseManager.getRequestHostname());
        return new ApiResponse("", 201);
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {       
        PageDetailsUpdate pageDetailsUpdate = requestResponseManager.getEntity(PageDetailsUpdate.class);
        pageMapper.updatePageDetails(pageDetailsUpdate);
        return new ApiResponse("",200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
