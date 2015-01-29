package ca.omny.all.configuration;

import ca.omny.auth.mappers.UserMapper;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.sites.mappers.SiteMapper;
import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;

public class DatabaseConfigurer {
    
    @Inject
    UserMapper userMapper;
    
    @Inject
    SiteMapper siteMapper;
    
    @Inject
    IDocumentQuerier querier;

    public void configure(DatabaseConfigurationValues configurationValues) throws IllegalAccessException {
        configureAdminUser(configurationValues.getAdminEmail());
        configureGoogleAuth(configurationValues.getGoogleCredentials());
        addAliases(configurationValues.getDomainAliases());
        addMappedDomains(configurationValues.getMappedDomains());
    }

    public void configureAdminUser(String adminEmail) throws IllegalAccessException {
        if (adminEmail != null) {
            String userId = userMapper.getIdFromEmail(adminEmail);
            if(userId==null) {
                userId = userMapper.createUser(adminEmail);
            }
            if(siteMapper.getSite("www")==null) {
                siteMapper.saveSite("www", "Main", userId);
            }
        }
    }

    public void addAliases(Map<String, String> domainAliases) {
        if (domainAliases != null) {
            for(String key: domainAliases.keySet()) {
                String dbKey = querier.getKey("domain_aliases",key);
                querier.set(dbKey, domainAliases.get(key));
            }
        }
    }

    public void addMappedDomains(Collection<String> mappedDomains) {
        if (mappedDomains != null) {
            for(String key: mappedDomains) {
                String dbKey = querier.getKey("mapped_domains",key);
                querier.set(dbKey, key);
            }
        }
    }

    public void configureGoogleAuth(GoogleCredentials credentials) {
        if (credentials != null) {
            credentials.setScope("https://www.googleapis.com/auth/userinfo.email");
            String dbKey = querier.getKey("credentials","oauth","google");
            querier.set(dbKey, credentials);
        }
    }
}
