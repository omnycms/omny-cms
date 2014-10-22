package ca.omny.storage;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.request.management.RequestResponseManager;
import java.util.Collection;
import javax.inject.Inject;

public class GlobalStorage {
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    StorageSystem storageSystem;
    
    @Inject
    IStorage storageImplementation;
    
    @Inject
    ConfigurationReader configurationReader;
    
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
