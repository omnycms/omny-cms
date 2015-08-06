package ca.omny.pages.mappers;

import com.google.gson.Gson;
import ca.omny.pages.models.PageTemplate;
import ca.omny.storage.StorageSystem;
import java.io.IOException;
import java.net.MalformedURLException;

public class PageTemplateMapper {

    public PageTemplate getPageTemplate(String themeName, String pageTemplateName, String host, boolean preview, StorageSystem storageSystem) throws MalformedURLException, IOException {
        Gson gson = new Gson();
        String versionFolder = preview ? "drafts" : "current";
        String pageTemplatePath = String.format("themes/%s/%s/template.json", versionFolder, themeName, pageTemplateName);

        String pageTemplateContent = storageSystem.getFileContents(pageTemplatePath,host);
        PageTemplate pageTemplate = gson.fromJson(pageTemplateContent, PageTemplate.class);

        return pageTemplate;
    }
}
