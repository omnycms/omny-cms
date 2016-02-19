package ca.omny.potent.site;

import ca.omny.db.IDocumentQuerier;
import ca.omny.potent.models.SiteConfiguration;

public class SiteConfigurationLoader {
    
    public SiteConfiguration getConfiguration(String hostname, IDocumentQuerier querier) {
        return querier.get("site_configuration", SiteConfiguration.class);
    }
    
   
}
