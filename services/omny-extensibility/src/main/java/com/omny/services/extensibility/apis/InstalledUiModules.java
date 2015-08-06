package com.omny.services.extensibility.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.services.extensibility.models.InstalledUiModuleConfiguration;
import com.omny.services.extensibility.mappers.InstalledUiModuleMapper;
import java.util.Collection;

public class InstalledUiModules implements OmnyApi {

    InstalledUiModuleMapper moduleMapper = new InstalledUiModuleMapper();
            
    @Override
    public String getBasePath() {
        return ExtensibilityApiConstants.base + "/ui/modules/installed/{creator}/{module}";
    }

    @Override
    public String[] getVersions() {
        return ExtensibilityApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String creator = requestResponseManager.getPathParameter("creator");
        String name = requestResponseManager.getPathParameter("module");
        String site = requestResponseManager.getRequestHostname();
        if(name!=null&&!name.isEmpty()) {
            return new ApiResponse(moduleMapper.getConfiguration(site, creator, name, requestResponseManager.getDatabaseQuerier()), 200);
        }
        Collection<InstalledUiModuleConfiguration> configurations = moduleMapper.listConfigurations(site, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse(configurations, 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        InstalledUiModuleConfiguration entity = requestResponseManager.getEntity(InstalledUiModuleConfiguration.class);
        entity.setName(requestResponseManager.getPathParameter("module"));
        entity.setCreator(requestResponseManager.getPathParameter("creator"));
        moduleMapper.saveConfiguration(
            requestResponseManager.getRequestHostname(),
            entity,
            requestResponseManager.getDatabaseQuerier()
        );
        return new ApiResponse("", 200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        String name = requestResponseManager.getPathParameter("module");
        String creator = requestResponseManager.getPathParameter("creator");
        String site = requestResponseManager.getRequestHostname();
        moduleMapper.deleteConfiguration(site, creator, name, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 200);
    }
    
}
