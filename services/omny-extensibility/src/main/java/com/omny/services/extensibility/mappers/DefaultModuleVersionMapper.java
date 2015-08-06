package com.omny.services.extensibility.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.models.InstalledUiModuleConfiguration;

public class DefaultModuleVersionMapper {
    
    public void setDefaultVersion(InstalledUiModuleConfiguration module, IDocumentQuerier querier) {
        String key = querier.getKey("default_module_version", module.getCreator(), module.getName());
        querier.set(key, module);
    }
    
    
    
}
