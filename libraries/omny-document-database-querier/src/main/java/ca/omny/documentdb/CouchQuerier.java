package ca.omny.documentdb;

import ca.omny.db.IDocumentQuerier;
import ca.omny.db.KeyValuePair;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.spy.memcached.util.StringUtils;

public class CouchQuerier implements IDocumentQuerier {

    CouchbaseClient client;

    public CouchQuerier() {
        this.client = CouchFactory.getClient();
    }

    public CouchQuerier(CouchbaseClient client) {
        this.client = client;
    }
    
    @Override
    public String getKey(String... keyParts) {
        String key = StringUtils.join(Arrays.asList(keyParts), "::");
        return key;
    }

    @Override
    public <T extends Object> T get(String key, Class<T> type) {
        Object o = client.get(key);
        if (o != null) {
            String value = o.toString();
            try {
                return getGson().fromJson(value, type);
            } catch (com.google.gson.JsonSyntaxException syntaxException) {
            }
            if (type.equals(String.class)) {
                return (T) value;
            }
        }

        return null;
    }

    @Override
    public void set(String key, Object value) {
        String gsonValue = this.getGson().toJson(value);
        client.set(key, gsonValue);
    }

    @Override
    public Object getRaw(String key) {
        return client.get(key);
    }
    
    @Override
    public void setRaw(String key, Object value) {
        client.set(key, value);
    }

    @Override
    public <T extends Object> List<T> getRange(String prefix, Class<T> type) {
        return this.getRange(prefix, type, true);
    }
    
    @Override
    public <T extends Object> List<T> getRange(String prefix, Class<T> type, boolean allowStale) {
        return this.getRange("all", "all", prefix, type, allowStale);
    }
    
    private <T extends Object> List<T> getRange(String designDocument, String viewName,String prefix, Class<T> type, boolean allowStale) {
        View view = client.getView(designDocument,viewName);
        Query query = new Query();
        query.setIncludeDocs(true);
        query.setRangeStart(prefix + "::");
        query.setRangeEnd(prefix + ":Z");
        if (!allowStale) {
            query.setStale(Stale.FALSE);
        }
        ViewResponse response = client.query(view, query);
        List<T> list = new LinkedList<T>();
        Gson g = this.getGson();
        for (ViewRow row : response) {
            if (row.getDocument() != null) {
                if (type.equals(String.class)) {
                    list.add((T)row.getDocument().toString());
                } else {
                    list.add(g.fromJson(row.getDocument().toString(), type));
                }
            }
        }
        return list;
    }
    
    public List<String> getKeysInRange(String prefix, boolean allowStale) {
        return this.getKeysInRange(prefix+"/", prefix+"0", allowStale);
    }
    
    public List<String> getKeysInRange(String start, String end, boolean allowStale) {
        return this.getKeysInRange("all", "all", start, end, allowStale, 0);
    }
    
     private List<String> getKeysInRange(String designDocument, String viewName,String start, String end, boolean allowStale, int groupLevel) {
        View view = client.getView(designDocument,viewName);
        Query query = new Query();
        query.setIncludeDocs(false);
        query.setRangeStart(start);
        query.setRangeEnd(end);
        if(groupLevel>0) {
            query.setGroupLevel(groupLevel);
        }
        if (!allowStale) {
            query.setStale(Stale.FALSE);
        }
        ViewResponse response = client.query(view, query);
        List<String> list = new LinkedList<String>();
        Gson g = this.getGson();
        for (ViewRow row : response) {
            list.add(row.getId());
        }
        return list;
    }

    /**
     * Does a multi-get and returns the keys received in the order they're
     * received
     *
     * @param <T>
     * @param keys
     * @param type
     * @return
     */
    @Override
    public <T extends Object> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type) {
        Map<String, Object> bulk = client.getBulk(keys);
        Gson g = this.getGson();
        Collection<KeyValuePair<T>> results = new LinkedList<KeyValuePair<T>>();
        for (String key : keys) {
            if (bulk.containsKey(key)) {
                Object o = bulk.get(key);
                System.out.println(o);
                T value = g.fromJson(o.toString(), type);
                KeyValuePair<T> pair = new KeyValuePair<T>(key, value);
                results.add(pair);
            }
        }
        return results;
    }

    @Override
    public <T extends Object> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type) {
        Map<String, Object> bulk = client.getBulk(keys);
        Gson g = this.getGson();
        Collection<T> results = new LinkedList<T>();
        for (String key : keys) {
            if (bulk.containsKey(key)) {
                Object o = bulk.get(key);
                System.out.println(o);
                T value = g.fromJson(o.toString(), type);
                results.add(value);
            }
        }
        return results;
    }

    public void delete(String key) {
        client.delete(key);
    }

    public void deleteAll(String prefix) {
        View view = client.getView("all", "all");
        Query query = new Query();
        query.setIncludeDocs(false);
        query.setRangeStart(prefix + "::");
        query.setRangeEnd(prefix + ":Z");
        ViewResponse response = client.query(view, query);
        for (ViewRow row : response) {
            this.delete(row.getId());
        }
    }

    private Gson getGson() {
        Gson g = new Gson();
        return g;
    }

    @Override
    public void finalize() {
        client.shutdown();
    }
    
    public void set(String key, Object value, int expires) {
        String gsonValue = this.getGson().toJson(value);
        client.set(key, expires, gsonValue);
    }
}
