package ca.omny.potent;

import ca.omny.documentdb.IDocumentQuerier;
import java.util.Map;
import javax.inject.Inject;

public class SiteOwnerMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    public boolean isSiteOwner(String hostname, String token) {
        String uid = this.getUid(token);
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
}
