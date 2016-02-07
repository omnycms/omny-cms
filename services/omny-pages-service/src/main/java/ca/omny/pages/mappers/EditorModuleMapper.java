package ca.omny.pages.mappers;

import ca.omny.pages.models.EditorModuleCollection;
import ca.omny.pages.models.EditorModuleInfo;
import ca.omny.db.IDocumentQuerier;
import java.util.Collection;

public class EditorModuleMapper {
    
    public Collection<EditorModuleInfo> getEditorModules(IDocumentQuerier querier) {
        String key = querier.getKey("module_editor","main_modules");
        return querier.get(key, EditorModuleCollection.class).getModules();
    }
}
