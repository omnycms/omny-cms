package ca.omny.pages.models;

public class PageTemplate {
    
    protected String templateName;
    protected String customTheme;
    protected String moduleListLocation;
    protected String titlePrefix;

    public String getCustomTheme() {
        return customTheme;
    }

    public void setCustomTheme(String customTheme) {
        this.customTheme = customTheme;
    }

    public String getModuleListLocation() {
        return moduleListLocation;
    }

    public void setModuleListLocation(String moduleListLocation) {
        this.moduleListLocation = moduleListLocation;
    }

    public String getTitlePrefix() {
        return titlePrefix;
    }

    public void setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
    }
}
