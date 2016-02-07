package ca.omny.documentdb.mappers;

import ca.omny.db.IDocumentQuerier;
import java.util.Collection;

public class SiteDataMapper<T>  {
    Class<T> dataClass;
    String prefix;
    
    public SiteDataMapper(Class<T> dataClass, String prefix) {
        this.dataClass = dataClass;
    }
  
    public Collection<T> list(String site, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site ,prefix);
        return querier.getRange(key, dataClass);
    }
    
    public T get(String site, String id, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site ,prefix, id);
        return querier.get(key, dataClass);
    }
    
    public void set(String site, String id, T instance, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site ,prefix, id);
        querier.set(key, instance);
    }
    
    public void delete(String site, String id, IDocumentQuerier querier) {
        String key = querier.getKey("site_data", site ,prefix, id);
        querier.delete(key);
    }
}
