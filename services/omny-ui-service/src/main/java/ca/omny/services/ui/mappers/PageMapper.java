package ca.omny.services.ui.mappers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import ca.omny.services.ui.themes.mappers.ThemeMapper;
import ca.omny.storage.IStorage;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class PageMapper {
    
    ThemeMapper themeMapper = new ThemeMapper();

    public String getPageContent(String host, String page, IStorage storage, IDocumentQuerier querier) {
        String baseUrl = ConfigurationReader.getDefaultConfigurationReader().getSimpleConfigurationString("OMNY_UI_CDN");
        if (baseUrl == null) {
            baseUrl = "";
        }
        String staticLocation = ConfigurationReader.getDefaultConfigurationReader().getSimpleConfigurationString("OMNY_STATIC_LOCATION");
        String base = getBase(baseUrl, staticLocation);
        if (page.startsWith("/")) {
            page = page.substring(1);
        }
        PageInfo info = getPageInfo(host, page, storage);
        String templateName = info.getTemplate() != null ? info.getTemplate() : "default";
        String themeName = info.getTheme() != null ? info.getTheme() : "default";
        if(themeName.equals("default")&&!host.equals("www")) {
            themeName = themeMapper.getDefaultTheme(host, querier);
        }
        String templateContent = getTemplateContent(host, themeName, templateName, storage);
        Map<String, String> pageSections = getPageSections(host, page, storage);

        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(templateContent), "template");
        mustache.execute(writer, pageSections);

        return base.replace("{{baseUrl}}", baseUrl).replace("{{content}}", StringEscapeUtils.unescapeHtml4(writer.toString()));
    }

    public String getBase(String baseUrl, String staticLocation) {
        try {
            URL url = new URL(baseUrl + "/index.html");
            InputStream resourceAsStream = url.openStream();

            return IOUtils.toString(resourceAsStream);
        } catch (IOException ex) {
            try {
                FileInputStream stream = new FileInputStream(staticLocation + "/index.html");
                InputStream resourceAsStream = stream;

                return IOUtils.toString(resourceAsStream);
            } catch (IOException ioException) {
                Logger.getLogger(PageMapper.class.getName()).log(Level.SEVERE, null, ioException);
            }
        }

        return null;
    }

    public String getTemplateContent(String host, String theme, String template, IStorage storage) {
        if(theme.startsWith("global")) {
            theme = theme.substring("global".length()+1);
            host = "www/global";
        }
        String themeFile = String.format("%s/ui/themes/%s/templates/%s/template.html", host, theme, template);
        return storage.getFileContents(themeFile);
    }

    public PageInfo getPageInfo(String host, String page, IStorage storage) {
        String file = String.format("%s/ui/pages/%s/info.json", host, page);
        String contents = storage.getFileContents(file);
        if (contents != null) {
            Gson gson = new Gson();
            return gson.fromJson(contents, PageInfo.class);
        }
        return new PageInfo();
    }
    
    public void writeSections(String host, String page, Map<String,String> sections, IStorage storage) {
        String sectionsFolder = String.format("%s/ui/pages/%s", host, page);
        for(String section: sections.keySet()) {
            String file = String.format("%s/%s.html", sectionsFolder, section);
            storage.saveFile(file, sections.get(section));
        }
    }

    public Map<String, String> getPageSections(String host, String page, IStorage storage) {
        HashMap<String, String> sections = new HashMap<>();
        String sectionsFolder = String.format("%s/ui/pages/%s", host, page);
        Collection<String> fileList = storage.getFileList(sectionsFolder, false, ".html");
        for (String file : fileList) {
            String contents = storage.getFileContents(sectionsFolder + "/" + file);
            
            final String section = file.substring(0, file.lastIndexOf(".html"));
            contents = String.format("<omny-section data-section=\"%s\">%s</omny-section>", section, contents);
            
            sections.put(section, contents);
        }
        return sections;
    }
}
