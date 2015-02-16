package com.omny.services.extensibility.mappers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.models.DevUiModuleConfiguration;
import ca.omny.services.extensibility.models.InstalledUiModuleConfiguration;
import javax.inject.Inject;

public class DefaultModuleVersionMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    ConfigurationReader configurationReader;
    
    public void setDefaultVersion(InstalledUiModuleConfiguration module) {
        String key = querier.getKey("default_module_version", module.getCreator(), module.getName());
        querier.set(key, module);
    }
    
    
    
}
