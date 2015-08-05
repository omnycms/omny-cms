package ca.omny.sites.mappers;

import ca.omny.configuration.ConfigurationReader;
import com.google.gson.Gson;
import ca.omny.sites.models.SiteMap;
import ca.omny.sites.models.SiteMapEntry;
import ca.omny.storage.StorageSystem;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SiteMapBuilder {
   
    ConfigurationReader configurationReader;
    public SiteMapBuilder() {
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    }
    
    
    public SiteMap getSiteMap(String hostname, StorageSystem storageSystem) {
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
