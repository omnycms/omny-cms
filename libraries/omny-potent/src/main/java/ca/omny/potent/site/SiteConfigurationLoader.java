package ca.omny.potent.site;

import ca.omny.db.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.potent.models.SiteConfiguration;

public class SiteConfigurationLoader {

    IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
    
    public SiteConfiguration getConfiguration(String hostname) {
        return querier.get("site_configuration", SiteConfiguration.class);
    }
    
   
}
