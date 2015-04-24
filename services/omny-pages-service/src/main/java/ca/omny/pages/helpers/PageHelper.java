package ca.omny.pages.helpers;

import ca.omny.pages.mappers.ModuleMapper;
import ca.omny.pages.mappers.PageMapper;
import ca.omny.pages.mappers.PageTemplateMapper;
import ca.omny.pages.mappers.ThemeMapper;
import ca.omny.pages.models.BuilderPluginInstanceInfo;
import ca.omny.pages.models.Page;
import ca.omny.pages.models.PageDetails;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.service.client.DiscoverableServiceClient;
import ca.omny.storage.StorageSystem;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.commons.lang3.StringEscapeUtils;

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
    
    @Inject
    DiscoverableServiceClient serviceClient;

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
        Map<String, Collection<BuilderPluginInstanceInfo>> pageModules = moduleMapper.getModules(pageModuleLocation, hostname);

        String versionFolder = preview ? "drafts" : "current";
        String templateModuleLocation = "themes/" + versionFolder + "/" + themeName + "/templates/" + page.getTemplateName() + "/modules.json";
        Map<String, Collection<BuilderPluginInstanceInfo>> templateModules = moduleMapper.getModules(templateModuleLocation, hostname);

        //Collection<Module> dependencies = moduleMapper.getAllDependencies(pageModules, templateModules);
        details.setPage(page);
        details.setThemeName(themeName);
        details.setThemeHtml(themeContent);
        details.setPageModules(pageModules);
        details.setTemplateModules(templateModules);
        //details.setDependencies(dependencies);
        return details;
    }

    public Map getPageBaseHtml(String hostname, String pageName, boolean preview) throws IOException {
        PageDetails pageDetails = this.getPageDetails(hostname, pageName, preview);

        StringBuilder headBuilder = new StringBuilder();
        StringBuilder bodyBuilder = new StringBuilder();
        String themeHtml = pageDetails.getThemeHtml();
        String themeName = pageDetails.getThemeName();
        Pattern p = Pattern.compile("\\{\\{[a-zA-Z]+\\}\\}");
        Matcher matcher = p.matcher(themeHtml);
        List<String> sections = new ArrayList<String>();
        
        Map siteDetails = this.getSiteDetails(hostname);
        headBuilder.append("<title>"+siteDetails.get("siteName")+" - "+pageDetails.getPage().getTitle()+"</title>");
        headBuilder.append(this.getCss(themeName));
        headBuilder.append(this.getScriptContent(pageDetails,themeName));
        while (matcher.find()) {
            String group = matcher.group();
            if(!group.equals("site.siteName")) {
                group = group.substring(2,group.length()-2);
                sections.add(group);
            }
        }
        HashMap<String, Object> sectionContent = this.getSectionContent(pageDetails, sections);
        sectionContent.put("site", siteDetails);
        
        themeHtml.replaceAll("site\\.siteName", siteDetails.get("siteName").toString());
        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(themeHtml),"template");
        mustache.execute(writer, sectionContent);
        bodyBuilder.append(StringEscapeUtils.unescapeHtml4(writer.toString()));
        Map<String,String> result = new HashMap<>();
        result.put("head", headBuilder.toString());
        result.put("body", bodyBuilder.toString());
        return result;
    }
    
    private Map getSiteDetails(String site) { 
        HashMap<String,String> queryParams = new HashMap<>();
        
        try {
            return serviceClient.get("sites", "/api/v1.0/sites/"+site, site, null, queryParams, null, Map.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PageHelper.class.getName()).log(Level.INFO, null, ex);
        }
        return null;
    }
    
    private String getScriptContent(PageDetails details, String themeName) {
        Gson gson = new Gson();
        return "<script>"+
                "var omnyPageModules="+gson.toJson(details.getPageModules())+";"+
                "var omnyTemplateModules="+gson.toJson(details.getTemplateModules())+";"+
                "require([\"themes/" + themeName + "/theme\"],function(theme){ theme.load();});"+
                "</script>";
    }
    
    private String getCss(String themeName) {
        return "<link rel=\"stylesheet\" href=\"/themes/" + themeName + "/theme.css\" />";
    }
    
    
    
    private HashMap<String, Object> getSectionContent(PageDetails details, List<String> sections) {
        HashMap<String, Object> sectionContent = new HashMap<>();
        for(String section: sections) {
            sectionContent.put(section, this.getSectionContent(details, section));
        }
        return sectionContent;
    }
    
    private String getSectionContent(PageDetails details, String section) {
        return "<div class=\"omny-module-section\" data-omny-type=\"section\" data-section=\""+section+"\">"
                    +"<div class=\"omny-template-section\" ></div><div class=\"omny-page-section\" >"
                    +"</div></div>";
    }

    public void markPagePublished(String path) {

    }
}
