package ca.omny.sites.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.sites.models.Site;
import ca.omny.sites.models.SiteDetails;
import ca.omny.sites.models.SiteUserAssociation;
import java.util.Collection;
import java.util.LinkedList;

public class SiteMapper {

    public Collection<SiteUserAssociation> getSites(String userId, IDocumentQuerier querier) {
        String prefix = querier.getKey("user_sites",userId);
        return querier.getRange(prefix, SiteUserAssociation.class);
    }
    
    public SiteDetails getSite(String site, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "site_details");
        return querier.get(key,SiteDetails.class);
    }
    
    public Collection<SiteDetails> getDetails(Collection<String> sites, IDocumentQuerier querier) {
        Collection<String> siteKeys = new LinkedList<String>();
        for(String site: sites) {
            String key = querier.getKey("site_data",site,"site_details");
            siteKeys.add(key);
        }
        
        return querier.multiGetCollection(siteKeys, SiteDetails.class);
    }

    public void saveSite(String subdomain, String siteName, String userId, IDocumentQuerier querier) throws IllegalAccessException {
        String key = querier.getKey("sites", subdomain);
        if (querier.get(key, Site.class) != null) {
            throw new IllegalAccessException("Site already exists");
        }
        Site site = new Site();
        site.setSite(subdomain);
        site.setSite(siteName);
        site.setUserId(userId);
        querier.set(key,site);
        addUserToSite(subdomain, site.getUserId(), querier);
        
        SiteDetails details = new SiteDetails();
        details.setSubdomain(subdomain);
        details.setSiteName(siteName);
        key = querier.getKey("site_data",subdomain,"site_details");
        querier.set(key, details);
    }
    
    public void addUserToSite(String siteName, String userId, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", siteName, "site_users",userId);
        SiteUserAssociation siteUserAssociation = new SiteUserAssociation();
        siteUserAssociation.setSite(siteName);
        siteUserAssociation.setUserId(userId);
        querier.set(key, siteUserAssociation);
        key = querier.getKey("user_sites",userId,siteName);
        querier.set(key, siteUserAssociation);
    }
    
    public void deleteSite(String siteName, IDocumentQuerier querier) {
        String key = querier.getKey("site_data",siteName,"site_users");
        Collection<SiteUserAssociation> userSiteAssocations = querier.getRange(key, SiteUserAssociation.class);
        for(SiteUserAssociation userSiteAssocation: userSiteAssocations) {
            key = querier.getKey("user_sites", userSiteAssocation.getUserId(),siteName);
            querier.delete(key);
        }
        key = querier.getKey("site_data",siteName);
        querier.deleteAll(key);
        key = querier.getKey("sites",siteName);
        querier.delete(key);
        
    }
}
