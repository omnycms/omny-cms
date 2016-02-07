package ca.omny.documentdb.mappers;

import ca.omny.db.IDocumentQuerier;
import java.util.Collection;

public class SimpleDataMapper<T> {
    String[] prefix;
    Class<T> dataClass;

    public SimpleDataMapper(String[] prefix, Class<T> dataClass) {
        this.prefix = prefix;
        this.dataClass = dataClass;
    }
    
    public Collection<T> list(IDocumentQuerier querier) {
        String key = querier.getKey(prefix);
        return list(key, querier);
    }
    
    public Collection<T> list(String key, IDocumentQuerier querier) {
        return querier.getRange(key, dataClass);
    }
    
    public T get(String id, IDocumentQuerier querier) {
        String key = querier.getKey(getKeyParts(id));
        return querier.get(key, dataClass);
    }
    
    public void set(String id, T instance, IDocumentQuerier querier) {
        String key = querier.getKey(getKeyParts(id));
        querier.set(key, instance);
    }
    
    public void delete(String id, T instance, IDocumentQuerier querier) {
        String key = querier.getKey(getKeyParts(id));
        querier.set(key, instance);
    }
    
    private String[] getKeyParts(String id) {
        String[] parts = new String[prefix.length+1];
        for(int i=0; i<prefix.length; i++) {
            parts[i] = prefix[i];
        }
        parts[parts.length-1] = id;
        return parts;
    }
}
