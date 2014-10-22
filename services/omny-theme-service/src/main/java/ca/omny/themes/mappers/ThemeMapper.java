package ca.omny.themes.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.service.client.DiscoverableServiceClient;
import ca.omny.storage.GlobalStorage;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import ca.omny.themes.models.Theme;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javax.inject.Inject;

public class ThemeMapper {

    @Inject
    IDocumentQuerier querier;
    
    @Inject
    StorageSystem storageSystem;
    
    @Inject
    GlobalStorage globalStorage;
    
    public Collection<String> getThemes(String hostname) {
        HashMap<String,Boolean> themeMap = new HashMap<String,Boolean>();
        Collection<String> files = storageSystem.listFiles("themes/current",true,hostname);
        for(String file: files) {
            String[] parts = file.split("/");
            themeMap.put(parts[0], Boolean.TRUE);
        }
        
        return themeMap.keySet();
    }
    
    public Collection<Theme> getInstallableThemes() {
        Collection<String> files = globalStorage.getGlobalItems("site-files/themes/current", "theme.html", true);
        LinkedList<Theme> themes = new LinkedList<Theme>();
        for(String file: files) {
            String[] parts = file.split("/");
            Theme theme = new Theme();
            theme.setName(parts[0]);
            themes.add(theme);
        }
        return themes;
    }
    
    public Theme getGlobalTheme(String themeName) {
        String themeData = globalStorage.getGlobalFileContents("site-files/themes/current/"+themeName+"/template.json");
        Gson gson = new Gson();
        Theme theme = gson.fromJson(themeData, Theme.class);
        
        return theme;
    }
    
    public void copyGlobalTheme(String source, String host, String destination) {
        globalStorage.copyFolderFromGlobal("site-files/themes/current/"+source, host, "themes/current/"+destination);
    }
    
    public void setDefaultTheme(String site, String theme) {
        String key = querier.getKey("default_theme",site);
        querier.set(key, theme);
    }
    
    public String getDefaultTheme(String site) {
        String key = querier.getKey("default_theme",site);
        return querier.get(key, String.class);
    }
}
