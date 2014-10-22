package ca.omny.pages.mappers;

import ca.omny.pages.models.EditorModuleCollection;
import ca.omny.pages.models.EditorModuleInfo;
import ca.omny.documentdb.IDocumentQuerier;
import java.util.Collection;
import javax.inject.Inject;

public class EditorModuleMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    public Collection<EditorModuleInfo> getEditorModules() {
        String key = querier.getKey("module_editor","main_modules");
        return querier.get(key, EditorModuleCollection.class).getModules();
    }
}
