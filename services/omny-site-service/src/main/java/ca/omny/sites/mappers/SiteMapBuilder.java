package ca.omny.sites.mappers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.service.client.DiscoverableServiceClient;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import ca.omny.sites.models.SiteMap;
import ca.omny.sites.models.SiteMapEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.inject.Inject;

public class SiteMapBuilder {
    
    @Inject
    DiscoverableServiceClient discoverableServiceClient;
    
    @Inject
    StorageSystem storageSystem;
    
    @Inject
    ConfigurationReader configurationReader;
    
    public SiteMap getSiteMap(String hostname) {
        Gson gson = new Gson();
        Map<String, String> parameters = new HashMap<String, String>();
        String suffix = "Data.json";
        
        Collection<String> files = storageSystem.listFiles("pages/current", true, suffix, hostname);
        
        if(!hostname.contains("\\.")) {
            if(hostname.equals("www")) {
                hostname += ".omny.ca";
            } else {
                hostname += ".omny.me";
            }
        }
        Collection<SiteMapEntry> entries = new LinkedList<SiteMapEntry>();
        for(String file: files) {
            String shortName = file.substring(0, file.length()-suffix.length());
            String loc = String.format("%s://%s/%s.html","http",hostname,shortName);
            SiteMapEntry entry = new SiteMapEntry();
            entry.setLoc(loc);
            entries.add(entry);
        }
        
        SiteMap siteMap = new SiteMap();
        siteMap.setSitemapEntries(entries);
        return siteMap;
    }
}
