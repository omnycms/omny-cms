package ca.omny.services.ui.themes.mappers;

import ca.omny.db.IDocumentQuerier;
import ca.omny.storage.GlobalStorage;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ThemeMapper {

    public Collection<String> getThemes(String hostname, StorageSystem storageSystem) {
        HashMap<String,Boolean> themeMap = new HashMap<String,Boolean>();
        Collection<String> files = storageSystem.listFiles("ui/themes",false,hostname);
        for(String file: files) {
            String[] parts = file.split("/");
            themeMap.put(parts[0], Boolean.TRUE);
        }
        
        return themeMap.keySet();
    }
    
    public Collection<Theme> getInstallableThemes(GlobalStorage globalStorage) {
        Collection<String> files = globalStorage.getGlobalItems("global/ui/themes", "", false);
        LinkedList<Theme> themes = new LinkedList<Theme>();
        for(String file: files) {
            String[] parts = file.split("/");
            Theme theme = new Theme();
            theme.setName(parts[0]);
            themes.add(theme);
        }
        return themes;
    }
    
    public Theme getGlobalTheme(String themeName, GlobalStorage globalStorage) {
        String themeData = globalStorage.getGlobalFileContents("global/ui/themes/"+themeName+"/info.json");
        Gson gson = new Gson();
        Theme theme = gson.fromJson(themeData, Theme.class);
        
        return theme;
    }
    
    public void setDefaultTheme(String site, String theme, IDocumentQuerier querier) {
        String key = querier.getKey("site_data",site,"default_theme");
        querier.set(key, theme);
    }
    
    public String getDefaultTheme(String site, IDocumentQuerier querier) {
        String key = querier.getKey("site_data",site,"default_theme");
        return querier.get(key, String.class);
    }
}
