package ca.omny.potent.site;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.models.SiteConfiguration;
import javax.inject.Inject;

public class SiteConfigurationLoader {
    
    @Inject
    IDocumentQuerier querier;
    
    public SiteConfiguration getConfiguration(String hostname) {
        return querier.get("site_configuration", SiteConfiguration.class);
    }
    
   
}
