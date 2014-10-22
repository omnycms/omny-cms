/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.omny.pages.models;

import java.util.Collection;
import java.util.Map;

public class PageModulesUpdate {
    String pageName;
    Map<String,Collection<BuilderPluginInstanceInfo>> pageModules;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Map<String, Collection<BuilderPluginInstanceInfo>> getPageModules() {
        return pageModules;
    }

    public void setPageModules(Map<String, Collection<BuilderPluginInstanceInfo>> pageModules) {
        this.pageModules = pageModules;
    }
}
