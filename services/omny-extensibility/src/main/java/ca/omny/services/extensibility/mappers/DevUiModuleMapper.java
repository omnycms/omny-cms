package ca.omny.services.extensibility.mappers;

import ca.omny.db.IDocumentQuerier;
import ca.omny.services.extensibility.models.DevUiModuleConfiguration;
import java.util.Collection;

public class DevUiModuleMapper {
    
    public DevUiModuleConfiguration getConfigurations(String site, String name, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "devmodules", name);
        return querier.get(key, DevUiModuleConfiguration.class);
    }
    
    public void deleteConfiguration(String site, String name, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "devmodules", name);
        querier.delete(key);
    }
    
    public Collection<DevUiModuleConfiguration> listConfigurations(String site, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "devmodules");
        return querier.getRange(key, DevUiModuleConfiguration.class);
    }
    
    public void saveConfiguration(String site, DevUiModuleConfiguration configuration, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "ui", "devmodules", configuration.getName());
        querier.set(key, configuration);
    }
    
}
