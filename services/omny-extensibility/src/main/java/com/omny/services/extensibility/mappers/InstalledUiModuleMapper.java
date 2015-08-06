package com.omny.services.extensibility.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.models.InstalledUiModuleConfiguration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class InstalledUiModuleMapper {
    
    public InstalledUiModuleConfiguration getConfiguration(String site, String creator, String name,  IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "installedmodules", creator, name);
        return querier.get(key, InstalledUiModuleConfiguration.class);
    }
    
    public Collection<InstalledUiModuleConfiguration> getVersioned(Collection<InstalledUiModuleConfiguration> configurations, IDocumentQuerier querier) {
        if(configurations.isEmpty()) {
            return new LinkedList<>();
        }
        List<String> keys = new LinkedList<>();
        for(InstalledUiModuleConfiguration configuration: configurations) {
            String key = querier.getKey("default_module_version", configuration.getCreator(), configuration.getName());
            keys.add(key);
        }
        return querier.multiGetCollection(keys, InstalledUiModuleConfiguration.class);
    }
    
    public void deleteConfiguration(String site, String creator, String name, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "installedmodules", creator, name);
        querier.delete(key);
    }
    
    public Collection<InstalledUiModuleConfiguration> listConfigurations(String site, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "installedmodules");
        return this.getVersioned(querier.getRange(key, InstalledUiModuleConfiguration.class),querier);
    }
    
    public void saveConfiguration(String site, InstalledUiModuleConfiguration configuration, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "installedmodules", configuration.getCreator(), configuration.getName());
        querier.set(key, configuration);
    }
    
}
