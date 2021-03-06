package ca.omny.db.extended;

import ca.omny.configuration.StringUtils;
import ca.omny.db.IDocumentQuerier;
import ca.omny.db.KeyValuePair;
import ca.omny.storage.IStorage;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class StorageBasedDatabase implements IDocumentQuerier {

    IStorage storage;

    public StorageBasedDatabase(IStorage storage) {
        this.storage = storage;
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }
    
    private static String DB_PREFIX = "www/db/";
    
    Gson gson = new Gson();
    
    @Override
    public void delete(String key) {
        storage.delete(DB_PREFIX+key);
    }

    public void deleteAll(String prefix) {
        storage.deleteFolder(DB_PREFIX+prefix);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String fileContents = storage.getFileContents(DB_PREFIX+key);
        if (type.equals(String.class)) {
            return (T) fileContents;
        }
        return gson.fromJson(fileContents, type);
    }
    
    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type) {
        Collection<String> fileList = storage.getFileList(DB_PREFIX+prefix, true, "");
        Collection<T> results = storage.fetchManyAsType(fileList, DB_PREFIX+prefix, type);
        return results;
    }

    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type, boolean allowStale) {
        return this.getRange(prefix, type);
    }

    @Override
    public Object getRaw(String key) {
        return storage.getFileContents(DB_PREFIX+key);
    }

    @Override
    public <T> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type) {
        Collection<KeyValuePair<T>> results = new LinkedList<>();
        for(String key: keys) {
            KeyValuePair<T> pair = new KeyValuePair<T>(key,this.get(key, type));
            results.add(pair);
        }
        return results;
    }

    @Override
    public <T> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type) {
        Collection<T> results = new LinkedList<>();
        for(String key: keys) {
            results.add(this.get(key, type));
        }
        return results;
    }

    @Override
    public String getKey(String... keyParts) {
        String key = StringUtils.join(keyParts, "/");
        return key;
    }

    @Override
    public void set(String key, Object value) {
        storage.saveFile(DB_PREFIX+key, value);
    }

    @Override
    public void setRaw(String key, Object value) {
        this.set(DB_PREFIX+key, value);
    }

    @Override
    public Collection<String> getKeysInRange(String prefix, boolean allowStale) {
        return storage.getFileList(DB_PREFIX+prefix, true, null);
    }

    @Override
    public Collection<String> getKeysInRange(String start, String end, boolean allowStale) {
        return this.getKeysInRange(start, allowStale);
    }
}
