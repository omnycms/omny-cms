package ca.omny.pages.mappers;

import com.google.gson.Gson;
import ca.omny.pages.models.Page;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.service.client.DiscoverableServiceClient;
import ca.omny.storage.StorageSystem;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.inject.Inject;

public class ThemeMapper {
    
    @Inject
    DiscoverableServiceClient serviceClient;

    @Inject
    IDocumentQuerier querier;
    
    @Inject
    StorageSystem storageSystem;

    public String getThemeHtml(String themeName, String host, boolean preview) throws MalformedURLException, IOException {
        Gson gson = new Gson();
        
        String versionFolder = preview?"drafts":"current";
        String themeTemplatePath = String.format("themes/%s/%s/theme.html", versionFolder, themeName);
        
        String themeContent = storageSystem.getFileContents(themeTemplatePath, host);
        
        return themeContent;
    }
    
    public String getThemeName(String hostname, Page page) {
        if(page.getCustomTheme()!=null) {
            return page.getCustomTheme();
        }
        String themeName = this.getDefaultTheme(hostname);
        return themeName;
    }
    
    public String getDefaultTheme(String hostname) {
        String key = querier.getKey("default_theme",hostname);
        String themeName = querier.get(key, String.class);
        if(themeName==null) {
            themeName = querier.get("default_theme", String.class);
        }
        return themeName;
    }
    
    public String getThemeCss(String themeName, String host, boolean preview) throws IOException {
        String versionFolder = preview?"drafts":"current";
        String themeCssPath = String.format("themes/%s/%s/theme.css", versionFolder, themeName);
        
        String themeCss = storageSystem.getFileContents(themeCssPath, host);
        return themeCss;
    }    
}
