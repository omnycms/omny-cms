package ca.omny.services.ui.themes.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.services.ui.apis.UiApiConstants;
import ca.omny.services.ui.themes.mappers.TemplateMapper;

public class Templates implements OmnyApi {

    @Override
    public String getBasePath() {
        return UiApiConstants.base+"/themes/{theme}/templates/{template}";
    }

    @Override
    public String[] getVersions() {
        return UiApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String theme = requestResponseManager.getPathParameter("theme");
        return new ApiResponse(new TemplateMapper().listTemplates(requestResponseManager.getRequestHostname(), theme, requestResponseManager.getStorageDevice()), 200);
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
