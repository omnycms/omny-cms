/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.omny.pages.models;

import java.util.Collection;
import java.util.Map;

public class PageDetails {
    Page page;
    PageTemplate pageTemplate;
    String themeName;
    String themeHtml;
    Map<String,Collection<BuilderPluginInstanceInfo>> pageModules;
    Map<String,Collection<BuilderPluginInstanceInfo>> templateModules;
    Collection<Module> dependencies;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeHtml() {
        return themeHtml;
    }

    public void setThemeHtml(String themeHtml) {
        this.themeHtml = themeHtml;
    }

    public Map<String, Collection<BuilderPluginInstanceInfo>> getPageModules() {
        return pageModules;
    }

    public void setPageModules(Map<String, Collection<BuilderPluginInstanceInfo>> pageModules) {
        this.pageModules = pageModules;
    }

    public Map<String, Collection<BuilderPluginInstanceInfo>> getTemplateModules() {
        return templateModules;
    }

    public void setTemplateModules(Map<String, Collection<BuilderPluginInstanceInfo>> templateModules) {
        this.templateModules = templateModules;
    }

    public Collection<Module> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Collection<Module> dependencies) {
        this.dependencies = dependencies;
    }
}
