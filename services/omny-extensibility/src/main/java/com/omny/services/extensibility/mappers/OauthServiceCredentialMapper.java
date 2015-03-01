package com.omny.services.extensibility.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.oauth.models.ServiceCredentials;
import java.util.Collection;
import javax.inject.Inject;

public class OauthServiceCredentialMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    public ServiceCredentials getServiceCredentials(String site, String serviceName) {
        String key = querier.getKey("ext", "credentials", site, serviceName);
        return querier.get(key, ServiceCredentials.class);
    }
    
    public void deleteServiceCredentials(String site, String serviceName) {
        String key = querier.getKey("ext", "credentials", site, serviceName);
        querier.delete(key);
    }
    
    public Collection<ServiceCredentials> listCredentials(String site) {
        String key = querier.getKey("ext", "credentials", site);
        return querier.getRange(key, ServiceCredentials.class);
    }
    
    public void saveCredentials(String site, String serviceName, ServiceCredentials credentials) {
       String key = querier.getKey("ext", "credentials", site, serviceName);
        querier.set(key, credentials);
    }
}
