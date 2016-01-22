package ca.omny.documentdb.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import java.util.Collection;

public class UserDataMapper<T>  {
    Class<T> dataClass;
    String prefix;
    
    public UserDataMapper(Class<T> dataClass, String prefix) {
        this.dataClass = dataClass;
    }
  
    public Collection<T> list(String site, String userId, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "user_data", userId ,prefix);
        return querier.getRange(key, dataClass);
    }
    
    public T get(String site, String userId, String id, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "user_data", userId ,prefix, id);
        return querier.get(key, dataClass);
    }
    
    public void set(String site, String userId, String id, T instance, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "user_data", userId ,prefix, id);
        querier.set(key, instance);
    }
    
    public void delete(String site, String userId, String id, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site, "user_data", userId ,prefix, id);
        querier.delete(key);
    }
}
