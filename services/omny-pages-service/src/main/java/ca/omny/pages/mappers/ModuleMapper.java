package ca.omny.pages.mappers;

import com.google.gson.Gson;
import ca.omny.pages.models.BuilderPluginInstanceInfo;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.storage.StorageSystem;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class ModuleMapper {
    
    @Inject
    StorageSystem storageSystem;
    
    @Inject
    IDocumentQuerier querier;
    
    public Map<String,Collection<BuilderPluginInstanceInfo>> getModules(String listLocation, String host) {
        Gson gson = new Gson();
        Type t = new TypeToken<Map<String,Collection<BuilderPluginInstanceInfo>>>(){}.getType();
        try {
            Map<String,Collection<BuilderPluginInstanceInfo>> plugins = gson.fromJson(storageSystem.getFileContents(listLocation, host),t);
            return plugins;
        } catch(Exception e) {
            
        }
        return new HashMap<String,Collection<BuilderPluginInstanceInfo>>();
    }
    
}
