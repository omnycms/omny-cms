
package ca.omny.documentdb;

import java.util.Collection;
import java.util.concurrent.Future;

public interface IDocumentQuerier {

    Future<Boolean> delete(String key);

    void deleteAll(String prefix);

    <T extends Object> T get(String key, Class<T> type);

    <T extends Object> Collection<T> getRange(String prefix, Class<T> type);

    <T extends Object> Collection<T> getRange(String prefix, Class<T> type, boolean allowStale);
    
    Collection<String> getKeysInRange(String prefix, boolean allowStale);
    
    Collection<String> getKeysInRange(String start, String end, boolean allowStale);

    Object getRaw(String key);

    <T extends Object> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type);

    <T extends Object> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type);
    
    String getKey(String... keyParts);

    void set(String key, Object value);

    void set(String key, Object value, int expires);

    void setRaw(String key, Object value);
    
}
