package ca.omny.services.ui.themes.mappers;

import ca.omny.storage.IStorage;
import java.util.Collection;

public class TemplateMapper {
    
    public Collection<String> listTemplates(String host, String theme, IStorage storage) {
        if(theme.startsWith("global")) {
            return listTemplates("www", theme.substring("global".length()+1), storage);
        }
        
        String location = String.format("%s/ui/themes/%s/templates", host, theme);
        return storage.listFolders(location);
    }
    
}
