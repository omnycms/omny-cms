package ca.omny.pages.models;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Al
 */
public class PluginDescriptor {
    Collection<Module> dependencies;
    Collection<String> jsToCache;
    Collection<String> cssToCache;
    
    public PluginDescriptor() {
        dependencies = new LinkedList<Module>();
    }
    
    public Collection<Module> getDependencies() {
        return dependencies;
    }
    
    public Collection<String> getJsToCache() {
        return jsToCache;
    }

    public void setJsToCache(Collection<String> jsToCache) {
        this.jsToCache = jsToCache;
    }

    public Collection<String> getCssToCache() {
        return cssToCache;
    }

    public void setCssToCache(Collection<String> cssToCache) {
        this.cssToCache = cssToCache;
    }
}
