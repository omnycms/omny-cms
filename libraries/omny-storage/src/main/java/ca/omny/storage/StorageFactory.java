package ca.omny.storage;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.CouchFactory;
import ca.omny.logger.OmnyLogger;
import ca.omny.storage.implementation.S3Storage;
import ca.omny.storage.implementation.LocalStorage;
import ca.omny.storage.implementation.DocumentDatabaseStorage;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class StorageFactory {
    
    @Inject
    ConfigurationReader configurationReader;
    
    S3Storage s3Storage;
    
    DocumentDatabaseStorage documentDbStorage;
    
    @Produces
    public IStorage getStorage(OmnyLogger logger) {
        String storageSystemName = configurationReader.getSimpleConfigurationString("storageSystem");
        
        if(storageSystemName.equals("local")) {
            String rootFolder = configurationReader.getSimpleConfigurationString("localFolder");
            LocalStorage localStorage = new LocalStorage(rootFolder);
            return localStorage;
        }
        if(storageSystemName.equals("s3")) {
            if(s3Storage==null) {
                s3Storage = new S3Storage(configurationReader);
            }
            return s3Storage;
        }
        if(documentDbStorage==null) {
            documentDbStorage = new DocumentDatabaseStorage();
            documentDbStorage.setConfigurationReader(configurationReader);
            documentDbStorage.setLogger(logger);
            documentDbStorage.setQuerier(CouchFactory.getQuerier());
        }
        
        return documentDbStorage;       
    }
}
