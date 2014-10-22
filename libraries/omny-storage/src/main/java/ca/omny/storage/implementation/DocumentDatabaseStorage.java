package ca.omny.storage.implementation;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.logger.OmnyLogger;
import com.google.gson.Gson;
import ca.omny.storage.IStorage;
import ca.omny.storage.MetaData;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@Alternative
public class DocumentDatabaseStorage implements IStorage {
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    OmnyLogger logger;
    
    @Inject
    ConfigurationReader configurationReader;
    
    public void setQuerier(IDocumentQuerier querier) {
        this.querier = querier;
    }

    public void setLogger(OmnyLogger logger) {
        this.logger = logger;
    }

    public void setConfigurationReader(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }
    
    @Override
    public MetaData getMetaData(String path) {
        String key = querier.getKey("metadata_storage",path);
        MetaData metaData = querier.get(key,MetaData.class);
        if(metaData==null) {
            return new MetaData();
        }
        return metaData;
    }
    
    @Override
    public String getFileContents(String fullPath) {
        boolean isJsonFile = fullPath.endsWith(".json");
        Object fileContents = this.getFileContents(fullPath, isJsonFile);
        if(fileContents==null) {
            return null;
        }
        if(!isJsonFile) {
            return new String((byte[])fileContents);
        }
        String result;
        if(fileContents instanceof byte[]) {
           result = new String((byte[])fileContents);
        } else {
            result = fileContents.toString();
        }
         
        return result;
    }
    
    @Override
    public Object getFileContents(String fullPath, boolean isJson) {
        long startTime = System.currentTimeMillis();
        String key = querier.getKey("storage",fullPath);
        Object contentBytes = querier.getRaw(key);
        //serviceClient.getApiResponse("storage", "/api/storage/Files", token, queryParameters, String.class);
        String content = null;
        if(contentBytes==null) {
            return null;
        }
        if (contentBytes instanceof String) {
            content = contentBytes.toString();
            if(!isJson) {
                try {
                    contentBytes = new Gson().fromJson(content, String.class);
                } catch (com.google.gson.JsonSyntaxException syntaxException) {
                }
            }
        } else {
            //content = new String((byte[]) contentBytes);
        }
        long endTime = System.currentTimeMillis();
        logger.info("took " + (endTime - startTime) + "ms to receive contents of " + fullPath);
        return contentBytes;
    }
    
    public void updateMetaData(String key) {
        MetaData metaData = new MetaData();
        metaData.setLastModified(this.getUnixTime());
        querier.set("metadata_"+key, metaData);
    }
    
    private long getUnixTime() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }
    
    @Override
    public void saveFile(String path, Object contents) {
        String key = querier.getKey("storage",path);
        this.updateMetaData(key);
        querier.set(key, contents);
    }
    
    @Override
    public void saveFileRaw(String path, byte[] contents) {
        String key = querier.getKey("storage",path);
        this.updateMetaData(key);
        querier.setRaw(key, contents);
    }
    
    @Override
    public void saveFileRaw(String path, Object contents) {
        String key = querier.getKey("storage",path);
        if(path.endsWith(".json")) {
            if(!(contents instanceof String)) {
                contents = new String((byte[])contents);
            }
        }
        this.updateMetaData(key);
        querier.setRaw(key, contents);
    }
    
    @Override
    public Collection<String> getFileList(String path, boolean recursive, String fileSuffix) {
        Collection<String> files = new LinkedList<String>();
        HashSet<String> folders = new HashSet<String>();
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        String prefix = querier.getKey("storage",path);
        Collection<String> keysInRange = querier.getKeysInRange(prefix, true);
        int prefixLength = prefix.length();
        for (String key : keysInRange) {
            String current = key.substring(prefixLength);
            if(!recursive) {
                String[] parts = current.split("/");
                String folder = parts[0];
                if(!folder.startsWith(".")) {
                    folders.add(folder);
                }
            }
            if(recursive||!current.contains("/")) {
                if(fileSuffix==null||current.endsWith(fileSuffix)) {
                    if(!current.startsWith(".")) {
                        files.add(current);
                    }
                }
            }
        }
        if(!recursive) {
           files.addAll(folders);
        }
        return files;
    }
    
    public Object getRawContents(String key) {
        return querier.getRaw(querier.getKey("storage",key));
    }
    
    @Override
    public void copyFile(String source, String destination) {
        Object contents = this.getRawContents(source);
        if(contents!=null) {
            this.saveFileRaw(destination, contents);
        }
    }

    @Override
    public void copyFolder(String source, String destination) {
        Collection<String> fileList = this.getFileList(source, true, null);
        for (String file : fileList) {
            if (!file.endsWith("/")) {
                this.copyFile(source + "/" + file, destination + "/" + file);
            }
        }
    }
    
    @Override
    public <T extends Object> Collection<T> fetchManyAsType(Collection<String> files, String basePath, Class<T> type) {
        Gson gson = new Gson();
        if(!basePath.isEmpty()&&!basePath.endsWith("/")) {
            basePath += "/";
        }
        Collection<String> keys = new LinkedList<String>();
        for(String file: files) {
            String key = querier.getKey("storage",basePath+file);
            keys.add(key);
        }
        Collection<T> result = querier.multiGetCollection(keys, type);
        return result;
    }

    @Override
    public void delete(String source) {
        String key = querier.getKey("storage",source);
        querier.delete(key);
    }
    
    @Override
    public void deleteFolder(String source) {
        String key = querier.getKey("storage",source);
        querier.deleteAll(key);
    }
}
