package ca.omny.pages.helpers;

import ca.omny.pages.mappers.ModuleMapper;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.mappers.PageTemplateMapper;
import ca.omny.pages.mappers.ThemeMapper;
import ca.omny.pages.models.BuilderPluginInstanceInfo;
import ca.omny.pages.models.Page;
import ca.omny.pages.models.PageDetails;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.storage.StorageSystem;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import javax.inject.Inject;

public class PageHelper {

    @Inject
    StorageSystem storageSystem;
    
    @Inject
    PageMapper pageMapper;
    
    @Inject
    PageTemplateMapper pageTemplateMapper;
    
    @Inject
    ModuleMapper moduleMapper;
    
    @Inject
    ThemeMapper themeMapper;

    @Inject
    IDocumentQuerier querier;

    public Collection<String> getPages(String path, String host) {
        String suffix = "Data.json";
       
        Collection<String> files = storageSystem.listFiles(path, true, suffix, host);

        Collection<String> pages = new LinkedList<String>();
        int length = suffix.length();
        for (String file : files) {
            pages.add(file.substring(0, file.length() - length));
        }
        return pages;
    }
    
    public PageDetails getPageDetails(String hostname, String pageName, boolean preview) throws IOException {
        Page page = pageMapper.getPage(pageName, hostname, preview);
        
        String themeName = themeMapper.getThemeName(hostname, page);
        //PageTemplate pageTemplate = pageTemplateMapper.getPageTemplate(themeName,page.getTemplateName(), preview);
        String themeContent = themeMapper.getThemeHtml(themeName, hostname, preview);
        PageDetails details = new PageDetails();
        
        String pageModuleLocation = "pages/current/" + pageName + "Modules.json";
        Map<String,Collection<BuilderPluginInstanceInfo>> pageModules = moduleMapper.getModules(pageModuleLocation, hostname);
        
        String versionFolder = preview ? "drafts" : "current";
        String templateModuleLocation = "themes/"+versionFolder+"/"+themeName+"/templates/"+page.getTemplateName()+"/modules.json";
        Map<String,Collection<BuilderPluginInstanceInfo>> templateModules = moduleMapper.getModules(templateModuleLocation, hostname);
        
        //Collection<Module> dependencies = moduleMapper.getAllDependencies(pageModules, templateModules);
        details.setPage(page);
        details.setThemeName(themeName);
        details.setThemeHtml(themeContent);
        details.setPageModules(pageModules);
        details.setTemplateModules(templateModules);
        //details.setDependencies(dependencies);
        return details;
    }

    public void markPagePublished(String path) {

    }
}
