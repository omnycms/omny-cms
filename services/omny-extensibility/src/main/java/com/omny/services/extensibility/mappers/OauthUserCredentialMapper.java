package com.omny.services.extensibility.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.oauth.models.UserCredentials;
import java.util.Collection;
import javax.inject.Inject;

/**
 * User credentials are stored with reference to the organization/site and name of the service they are using
 * so that a module can manage credentials for the service it is associated with
 */
public class OauthUserCredentialMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    public UserCredentials getCredentials(String site, String organization, String serviceName, String name) {
        String key =  querier.getKey("site_data", site, "ext_credentials", organization, serviceName, name);
        return querier.get(key, UserCredentials.class);
    }
    
    public void deleteCredentials(String site, String organization, String serviceName, String name) {
        String key =  querier.getKey("site_data", site, "ext_credentials", organization, serviceName, name);
        querier.delete(key);
    }
    
    public Collection<UserCredentials> listCredentials(String site) {
        String key =  querier.getKey("site_data", site, "ext_credentials");
        return querier.getRange(key, UserCredentials.class);
    }
    
    public Collection<UserCredentials> listCredentials(String site, String organization, String serviceName) {
        String key =  querier.getKey("site_data", site, "ext_credentials", organization, serviceName);
        return querier.getRange(key, UserCredentials.class);
    }
    
    public void saveCredentials(String site, String organization, String serviceName, String name, UserCredentials credentials) {
        String key =  querier.getKey("site_data", site, "ext_credentials", organization, serviceName, name);
        querier.set(key, credentials);
    }
}
