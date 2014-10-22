package ca.omny.sites;

import ca.omny.auth.sessions.SessionMapper;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.service.client.DiscoverableServiceClient;
import ca.omny.service.client.PermissionChecker;
import ca.omny.sites.mappers.SiteMapper;
import ca.omny.sites.models.SiteDetails;
import ca.omny.sites.models.SiteUserAssociation;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

public class SiteHelper {
    
    @Inject
    SiteMapper siteMapper;
    
    @Inject
    PermissionChecker permissionChecker;
    
    @Inject
    ConfigurationReader configurationReader;
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    DiscoverableServiceClient serviceClient;
    
    public Collection<SiteDetails> getSites(String userId) {
        Collection<SiteUserAssociation> sites = siteMapper.getSites(userId);
        Collection<String> siteList = new LinkedList<String>();
        for(SiteUserAssociation association: sites) {
            siteList.add(association.getSite());
        }
        Collection<SiteDetails> details = siteMapper.getDetails(siteList);
        return details;
    }
    
    public void createSite(String subdomain, String siteName, String userId) throws IllegalAccessException {
        siteMapper.saveSite(subdomain, siteName, userId);
    }
}
