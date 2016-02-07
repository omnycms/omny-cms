package ca.omny.storage;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import java.util.Collection;

public class GlobalStorage {
    
    IDocumentQuerier querier;
    
    StorageSystem storageSystem;
    
    IStorage storageImplementation;
    
    ConfigurationReader configurationReader;
    
    static GlobalStorage defaultGlobalStorage;
   
    public GlobalStorage(IDocumentQuerier querier, StorageSystem storageSystem, IStorage storageImplementation, ConfigurationReader configurationReader) {
        this.querier = querier;
        this.storageSystem = storageSystem;
        this.storageImplementation = storageImplementation;
        this.configurationReader = configurationReader;
    }
    
    public String getGlobalFileContents(String path) {
        return storageImplementation.getFileContents("www/"+path);
    }
    
    public Object getGlobalFileContents(String path, boolean isJson) {
        return storageImplementation.getFileContents("www/"+path, isJson);
    }
    
    public Collection<String> getGlobalItems(String baseFolder, String suffix, boolean recursive) {
        Collection<String> files = storageSystem.listFiles(baseFolder,true, suffix, "www");
        
        return files;
    }
    
    public <T extends Object> Collection<T> getFilesOfType(String baseFolder, String suffix, boolean recursive, Class<T> type) {
        Collection<String> files = this.getGlobalItems(baseFolder, suffix, recursive);
        return storageImplementation.fetchManyAsType(files, "www/"+baseFolder, type);
    }
    
    public void copyFolderFromGlobal(String globalFolder, String hostname, String localFolder) {
        storageImplementation.copyFolder("www/"+globalFolder, hostname+"/"+localFolder);
    }
    
}
