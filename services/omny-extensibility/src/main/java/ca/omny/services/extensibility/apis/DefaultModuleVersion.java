package ca.omny.services.extensibility.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.services.extensibility.models.InstalledUiModuleConfiguration;
import ca.omny.services.extensibility.mappers.DefaultModuleVersionMapper;

public class DefaultModuleVersion implements OmnyApi {

    DefaultModuleVersionMapper moduleMapper = new DefaultModuleVersionMapper();
    
    @Override
    public String getBasePath() {
        return ExtensibilityApiConstants.base + "/ui/modules/{module}/versions/default";
    }

    @Override
    public String[] getVersions() {
        return ExtensibilityApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        InstalledUiModuleConfiguration entity = requestResponseManager.getEntity(InstalledUiModuleConfiguration.class);
        entity.setName(requestResponseManager.getPathParameter("module"));
        entity.setCreator(requestResponseManager.getRequestHostname());
        moduleMapper.setDefaultVersion(entity, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
