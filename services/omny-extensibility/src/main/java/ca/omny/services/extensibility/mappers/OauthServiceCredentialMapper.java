package ca.omny.services.extensibility.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.services.extensibility.oauth.models.ServiceCredentials;
import java.util.Collection;
import javax.inject.Inject;

public class OauthServiceCredentialMapper {
    
    public ServiceCredentials getServiceCredentials(String site, String serviceName, IDocumentQuerier querier) {
        String key = querier.getKey("ext", "credentials", site, serviceName);
        return querier.get(key, ServiceCredentials.class);
    }
    
    public void deleteServiceCredentials(String site, String serviceName, IDocumentQuerier querier) {
        String key = querier.getKey("ext", "credentials", site, serviceName);
        querier.delete(key);
    }
    
    public Collection<ServiceCredentials> listCredentials(String site, IDocumentQuerier querier) {
        String key = querier.getKey("ext", "credentials", site);
        return querier.getRange(key, ServiceCredentials.class);
    }
    
    public void saveCredentials(String site, String serviceName, ServiceCredentials credentials, IDocumentQuerier querier) {
       String key = querier.getKey("ext", "credentials", site, serviceName);
        querier.set(key, credentials);
    }
}
