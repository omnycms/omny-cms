package ca.omny.sites;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.sites.mappers.SiteMapper;
import ca.omny.sites.models.SiteDetails;
import ca.omny.sites.models.SiteUserAssociation;
import java.util.Collection;
import java.util.LinkedList;

public class SiteHelper {
    
    SiteMapper siteMapper;
    
    public SiteHelper() {
        siteMapper = new SiteMapper();
    }
    
    public Collection<SiteDetails> getSites(String userId, IDocumentQuerier querier) {
        Collection<SiteUserAssociation> sites = siteMapper.getSites(userId, querier);
        Collection<String> siteList = new LinkedList<String>();
        for(SiteUserAssociation association: sites) {
            siteList.add(association.getSite());
        }
        Collection<SiteDetails> details = siteMapper.getDetails(siteList, querier);
        return details;
    }
    
    public void createSite(String subdomain, String siteName, String userId, IDocumentQuerier querier) throws IllegalAccessException {
        siteMapper.saveSite(subdomain, siteName, userId, querier);
    }
}
