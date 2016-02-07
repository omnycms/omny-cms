package ca.omny.potent;

import ca.omny.db.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import java.util.Map;

public class SiteOwnerMapper {
    IDocumentQuerier querier;
    
    public SiteOwnerMapper() {
        querier = QuerierFactory.getDefaultQuerier();
    }
    
    public boolean isSiteOwner(String hostname, String uid) {
        String key = querier.getKey("sites",hostname);
        Map site = querier.get(key, Map.class);
        if(site!=null && site.containsKey("userId")) {
           return site.get("userId").equals(uid);
        }
        return false;
    }
    
    public String getUid(String token) {
        String key = querier.getKey("sessions",token);
        Map session = querier.get(key, Map.class);
        if(session!=null&&session.containsKey("userId")) {
            return session.get("userId").toString();
        }
        return null;
    }
    
    public boolean isSiteUser(String hostname,String uid) {
        String key = querier.getKey("site_data",hostname,"site_data",uid);
        return querier.get(key,Map.class)!=null;
    }
}
