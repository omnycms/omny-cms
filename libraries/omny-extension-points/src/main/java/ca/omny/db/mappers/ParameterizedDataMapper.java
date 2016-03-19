package ca.omny.db.mappers;

import ca.omny.db.IDocumentQuerier;
import java.util.Collection;

public class ParameterizedDataMapper<T> {
    String[] prefix;
    Class<T> dataClass;

    public ParameterizedDataMapper(String[] prefix, Class<T> dataClass) {
        this.prefix = prefix;
        this.dataClass = dataClass;
    }
    
    public Collection<T> list(IDocumentQuerier querier, String... parameters) {
        String key = querier.getKey(getKeyParts(parameters));
        return list(key, querier);
    }
    
    public Collection<T> list(String key, IDocumentQuerier querier) {
        return querier.getRange(key, dataClass);
    }
    
    public T get(IDocumentQuerier querier, String... parameters) {
        String key = querier.getKey(getKeyParts(parameters));
        return querier.get(key, dataClass);
    }
    
    public void set(T instance, IDocumentQuerier querier, String... parameters) {
        String key = querier.getKey(getKeyParts(parameters));
        querier.set(key, instance);
    }
    
    public void delete(T instance, IDocumentQuerier querier, String... parameters) {
        String key = querier.getKey(getKeyParts(parameters));
        querier.set(key, instance);
    }
    
    private String[] getKeyParts(String[] parameters) {
        String[] parts = new String[prefix.length];
        int count = 0;
        for(int i=0; i<prefix.length; i++) {
            if(prefix[i].startsWith("{")) {
                parts[i] = parameters[count];
                count++;
            } else {
                parts[i] = prefix[i];
            }
        }
        return parts;
    }
}
