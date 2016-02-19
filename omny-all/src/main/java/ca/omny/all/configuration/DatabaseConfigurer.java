package ca.omny.all.configuration;

import ca.omny.auth.mappers.UserMapper;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import ca.omny.sites.mappers.SiteMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class DatabaseConfigurer {
    
    UserMapper userMapper = new UserMapper();
    SiteMapper siteMapper = new SiteMapper();
    IDocumentQuerier querier;

    public DatabaseConfigurer(IDocumentQuerier querier) {
        this.querier = querier;
    }

    public void configure(DatabaseConfigurationValues configurationValues) throws IllegalAccessException {
        configureAdminUser(configurationValues.getAdminEmail());
        configureGoogleAuth(configurationValues.getGoogleCredentials());
        addAliases(configurationValues.getDomainAliases());
        addMappedDomains(configurationValues.getMappedDomains());
        if(configurationValues.shouldUpdateDefaultDbValues()) {
            updateDefaultDbValues();
        }
    }
    
    public void updateDefaultDbValues() {
        File defaultDbValuesRootDirectory = ConfigurationReader.findRootDirectory("default-db-values");
        if(defaultDbValuesRootDirectory != null) {
            updateDefaultDbValues(defaultDbValuesRootDirectory, "");
        }
    }
    
    private void updateDefaultDbValues(File currentDir, String keyBase) {
        for(File f: currentDir.listFiles()) {
            String key = querier.getKey(keyBase,f.getName());
            if(keyBase.equals("")) {
                key = f.getName();
            }
            if(f.isDirectory()) {
                updateDefaultDbValues(currentDir, key);
            } else {
                try {
                    querier.set(key, FileUtils.readFileToString(f));
                } catch (IOException ex) {
                    Logger.getLogger(DatabaseConfigurer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void configureAdminUser(String adminEmail) throws IllegalAccessException {
        if (adminEmail != null) {
            String userId = userMapper.getIdFromEmail(adminEmail, querier);
            if(userId==null) {
                userId = userMapper.createUser(adminEmail, querier);
            }
            if(siteMapper.getSite("www",querier)==null) {
                siteMapper.saveSite("www", "Main", userId, querier);
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
