package ca.omny.request.management;

import ca.omny.db.IDocumentQuerier;
import java.util.Map;

public class SiteOwnerMapper {
    
    public boolean isSiteOwner(String hostname, String uid, IDocumentQuerier querier) {
        String key = querier.getKey("sites",hostname);
        Map site = querier.get(key, Map.class);
        if(site!=null && site.containsKey("userId")) {
           return site.get("userId").equals(uid);
        }
        return false;
    }
    
    public String getUid(String token, IDocumentQuerier querier) {
        String key = querier.getKey("sessions",token);
        Map session = querier.get(key, Map.class);
        if(session!=null&&session.containsKey("userId")) {
            return session.get("userId").toString();
        }
        return null;
    }
    
    public boolean isSiteUser(String hostname,String uid, IDocumentQuerier querier) {
        String key = querier.getKey("site_data",hostname,"site_data",uid);
        return querier.get(key,Map.class)!=null;
    }
}
