package ca.omny.services.ui.themes.mappers;

import ca.omny.db.IDocumentQuerier;
import ca.omny.storage.IStorage;
import java.util.Collection;
import java.util.LinkedList;

public class ThemeMapper {

    public Collection<String> getThemes(String hostname, IStorage storageSystem) {
        LinkedList<Theme> themes = new LinkedList<Theme>();
        Collection<String> files = storageSystem.listFolders(String.format("%s/ui/themes",hostname), false);        
        return files;
    }
    
    public Collection<Theme> getInstallableThemes(IStorage storage) {
        Collection<String> folders = storage.listFolders("www/global/ui/themes", false);
        LinkedList<Theme> themes = new LinkedList<Theme>();
        for(String file: folders) {           
            Theme theme = new Theme();
            theme.setName(file);
            themes.add(theme);
        }
        return themes;
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
