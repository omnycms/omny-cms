package com.omny.services.extensibility.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.services.extensibility.models.DevUiModuleConfiguration;
import com.omny.services.extensibility.mappers.DevUiModuleMapper;
import java.util.Collection;

public class DevUiModules implements OmnyApi {

    DevUiModuleMapper moduleMapper = new DevUiModuleMapper();

    @Override
    public String getBasePath() {
        return ExtensibilityApiConstants.base + "/ui/devmodules/{configuration}";
    }

    @Override
    public String[] getVersions() {
        return ExtensibilityApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String name = requestResponseManager.getPathParameter("configuration");
        String site = requestResponseManager.getRequestHostname();
        if(name!=null) {
            return new ApiResponse(moduleMapper.getConfigurations(site, name, requestResponseManager.getDatabaseQuerier()), 200);
        }
        Collection<DevUiModuleConfiguration> configurations = moduleMapper.listConfigurations(site, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse(configurations, 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        DevUiModuleConfiguration entity = requestResponseManager.getEntity(DevUiModuleConfiguration.class);
        entity.setName(requestResponseManager.getPathParameter("configuration"));
        moduleMapper.saveConfiguration(
            requestResponseManager.getRequestHostname(),
            entity,
            requestResponseManager.getDatabaseQuerier()
        );
        return new ApiResponse("", 200);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        String name = requestResponseManager.getPathParameter("configuration");
        String site = requestResponseManager.getRequestHostname();
        moduleMapper.deleteConfiguration(site, name, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 200);
    }

}
