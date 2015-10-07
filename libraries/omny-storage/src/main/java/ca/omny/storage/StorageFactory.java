package ca.omny.storage;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.CouchFactory;
import ca.omny.logger.OmnyLogger;
import ca.omny.logger.SimpleLogger;
import ca.omny.storage.implementation.S3Storage;
import ca.omny.storage.implementation.LocalStorage;
import ca.omny.storage.implementation.DocumentDatabaseStorage;
import java.io.File;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class StorageFactory {
    
    @Inject
    ConfigurationReader configurationReader;
    
    static OmnyLogger logger = new SimpleLogger(StorageFactory.class.getName());
    static S3Storage s3Storage;
    
    static DocumentDatabaseStorage documentDbStorage;
    
    static IStorage defaultStorage;
    static ConfigurationReader configReader = ConfigurationReader.getDefaultConfigurationReader();
    
    public static IStorage getDefaultStorage() {
        if(defaultStorage==null) {
            defaultStorage = getDefaultStorage(logger,configReader);
        }
        return defaultStorage;
    }
    
    @Produces
    public IStorage getStorage(OmnyLogger logger) {
        return getDefaultStorage(logger, configurationReader);
    }
    
    public static IStorage getDefaultStorage(OmnyLogger logger, ConfigurationReader configurationReader) {
        String storageSystemName = configurationReader.getSimpleConfigurationString("OMNY_STORAGE_SYSTEM");
        if(storageSystemName!=null) {
            if(storageSystemName.equals("DATABASE")) {
                if(documentDbStorage==null) {
                    documentDbStorage = new DocumentDatabaseStorage();
                    documentDbStorage.setConfigurationReader(configurationReader);
                    documentDbStorage.setLogger(logger);
                    documentDbStorage.setQuerier(CouchFactory.getQuerier());
                }
                return documentDbStorage;   
            }
            if(storageSystemName.equals("S3")) {
                if(s3Storage==null) {
                    s3Storage = new S3Storage(configurationReader);
                }
                return s3Storage;
            }   
        }
        
        String rootFolder = configurationReader.getSimpleConfigurationString("OMNY_LOCAL_FOLDER");
        if(rootFolder==null || rootFolder.equals(".")) {
            File findRootDirectory = ConfigurationReader.findRootDirectory("storage");
            if(findRootDirectory!=null) {
                rootFolder = findRootDirectory.getAbsolutePath();
            } else {
                rootFolder = ".";
            }
        }
        LocalStorage localStorage = new LocalStorage(rootFolder);
        return localStorage;
  
    }
    
    
}
