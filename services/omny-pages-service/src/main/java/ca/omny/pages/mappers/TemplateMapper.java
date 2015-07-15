package ca.omny.pages.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import javax.inject.Inject;

public class TemplateMapper {
    @Inject
    IDocumentQuerier querier;
    
    public String getTemplateLocation(String theme, String pageTemplate, String hostname, boolean preview) {
        String versionFolder = preview ? "drafts" : "current";
        if(pageTemplate.startsWith("site/")) {
            return "templates/"+versionFolder+"/"+pageTemplate.substring("site/".length())+"/modules.json";
        }
        String templateModuleLocation = "themes/" + versionFolder + "/" + theme + "/templates/" + pageTemplate + "/modules.json";
        return templateModuleLocation;
    }
}
