package ca.omny.db;

import java.util.Collection;

public interface IDocumentQuerier {

    <T extends Object> T get(String key, Class<T> type);

    <T extends Object> Collection<T> getRange(String prefix, Class<T> type);

    <T extends Object> Collection<T> getRange(String prefix, Class<T> type, boolean allowStale);
    
    Collection<String> getKeysInRange(String prefix, boolean allowStale);
    
    Collection<String> getKeysInRange(String start, String end, boolean allowStale);

    Object getRaw(String key);

    <T extends Object> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type);

    <T extends Object> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type);
    
    String getKey(String... keyParts);
    
    void delete(String key);

    void set(String key, Object value);

    void setRaw(String key, Object value);
    
}
