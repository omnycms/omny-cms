package ca.omny.utilities.testing.db;

import ca.omny.db.IDocumentQuerier;
import ca.omny.db.KeyValuePair;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import net.spy.memcached.util.StringUtils;

public class MemoryDatabase implements IDocumentQuerier {

    Map<String,MemoryDatabase> subDbs;
    Map<String,Object> values;
    Gson gson = new Gson();
    
    public MemoryDatabase() {
        subDbs = new TreeMap<>();
        values = new TreeMap<>();
    }
    
    MemoryDatabase getRelevantDb(String key) {
        return getRelevantDb(key, false);
    }
    
    MemoryDatabase getRelevantDb(String key, boolean create) {
        String[] keys = key.split("/");
        if(keys.length==1) {
            return this;
        }
        return getRelevantDb(keys, create, 0);
    }
    
    MemoryDatabase getRelevantDb(String[] keys, boolean create, int index) {
        String currentKey = keys[index];
        if(create && !subDbs.containsKey(currentKey)) {
            subDbs.put(currentKey, new MemoryDatabase());
        }
        MemoryDatabase currentDb = subDbs.get(currentKey);
        if(index + 2 == keys.length) {
            return currentDb;
        }
        return currentDb.getRelevantDb(keys, create, index+1);
    }
    
    Object getValue(String key) {
        return values.get(key);
    }
    
    <T> Collection<T> getAll(Class<T> type) {
        LinkedList<T> list = new LinkedList<T>();
        for(String key: values.keySet()) {
            list.add(getCorrectType(values.get(key), type));
        }
        return list;
    }
    
    private String getLastPart(String key) {
        String[] parts = key.split("/");
        return parts[parts.length-1];
    }
    
    private <T> T getCorrectType(Object value, Class<T> type) {
        return gson.fromJson(gson.toJson(value), type);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        MemoryDatabase relevantDb = getRelevantDb(key);
        if(relevantDb!=null) {
            Object value = relevantDb.getValue(getLastPart(key));
            //may seem crazy, but allows converting old types to new types
            return getCorrectType(value, type);
        }
        
        return null;
    }

    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type) {
        MemoryDatabase relevantDb = getRelevantDb(prefix);
        return relevantDb.getAll(type);
    }

    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type, boolean allowStale) {
        return getRange(prefix, type);
    }

    @Override
    public Collection<String> getKeysInRange(String prefix, boolean allowStale) {
        MemoryDatabase relevantDb = getRelevantDb(prefix);
        if(relevantDb != null) {
            return relevantDb.values.keySet();
        }
        return null;
    }

    @Override
    public Collection<String> getKeysInRange(String start, String end, boolean allowStale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getRaw(String key) {
        MemoryDatabase relevantDb = getRelevantDb(key);
        if(relevantDb!=null) {
            return relevantDb.values.get(getLastPart(key));
        }
        
        return null;
    }

    @Override
    public <T> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getKey(String... keyParts) {
        String key = StringUtils.join(Arrays.asList(keyParts), "::");
        return key;
    }

    @Override
    public void delete(String key) {
        MemoryDatabase relevantDb = getRelevantDb(key);
        if(relevantDb!=null) {
            relevantDb.values.remove(getLastPart(key));
        }
    }

    @Override
    public void set(String key, Object value) {
        MemoryDatabase relevantDb = getRelevantDb(key, true);
        if(relevantDb!=null) {
            relevantDb.values.put(getLastPart(key), value);
        } 
    }

    @Override
    public void setRaw(String key, Object value) {
        set(key,value);
    }
        
    
}
