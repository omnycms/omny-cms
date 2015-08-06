package ca.omny.pages.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import com.google.gson.Gson;
import ca.omny.pages.models.CreatePageRequest;
import ca.omny.pages.models.Page;
import ca.omny.pages.models.PageDetailsUpdate;
import ca.omny.pages.models.PageModulesUpdate;
import ca.omny.pages.models.SampleData;
import ca.omny.storage.StorageSystem;
import java.io.IOException;
import java.net.MalformedURLException;

public class PageMapper {
    
    Gson gson = new Gson();
    
    ThemeMapper themeMapper;
    
    public PageMapper() {
        this(new ThemeMapper());
    }
    
    public PageMapper(ThemeMapper themeMapper) {
        this.themeMapper = themeMapper;
    }
    
    public Page getPage(String pageName, String host, StorageSystem storageSystem, boolean preview) throws MalformedURLException, IOException {
        Gson gson = new Gson();
        String versionFolder = preview?"drafts":"current";
        String pagePath = String.format("pages/%s/%sData.json", versionFolder,pageName);
        System.out.println("fetching data for "+pagePath+" "+host);
        String pageContent = storageSystem.getFileContents(pagePath, host);
        Page page = gson.fromJson(pageContent, Page.class);
        
        return page;
    }
    
    public void updateMetaData(Page page, String pageName, String host, StorageSystem storageSystem, boolean preview) {
        Gson gson = new Gson();
        String versionFolder = preview?"drafts":"current";
        String pagePath = String.format("pages/%s/%sData.json", versionFolder,pageName);
        storageSystem.saveFile(pagePath, gson.toJson(page), host);
    }
    
    public void updatePageDetails(PageDetailsUpdate update) {
        
    }
    
    public void updateModules(PageModulesUpdate pageModulesUpdate, String host, StorageSystem storageSystem) {
        String pageLocation = "pages/current/"+pageModulesUpdate.getPageName();
        storageSystem.saveFile(pageLocation+"Modules.json", pageModulesUpdate.getPageModules(), host);
    }
    
    public void createPage(CreatePageRequest createPageRequest, String hostname, StorageSystem storageSystem, IDocumentQuerier querier) {
        if(createPageRequest.getFromSample().getTheme()==null) {
            createPageRequest.getFromSample().setTheme(themeMapper.getDefaultTheme(hostname, querier));
        }
        Page page = createPageRequest.getPageDetails();
        String sampleLocation = "themes/current/"+createPageRequest.getFromSample().getTheme()+"/sample-pages/"+createPageRequest.getFromSample().getSampleName();
        String contents = storageSystem.getFileContents(sampleLocation+"/data.json", hostname);
        SampleData sampleData = gson.fromJson(contents,SampleData.class);
        page.setTemplateName(sampleData.getTemplate());
        
        String pageLocation = "pages/current/"+createPageRequest.getName();
        storageSystem.saveFile(pageLocation+"Data.json", page, hostname);
        
        storageSystem.copyFile(sampleLocation+"/modules.json", pageLocation+"Modules.json", hostname);
    }
}
