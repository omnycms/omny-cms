package ca.omny.pages.models;

public class Page {

    protected String customTheme;
    protected String moduleListLocation;
    protected String title;
    protected String templateName;

    public String getCustomTheme() {
        return customTheme;
    }

    public void setCustomTheme(String customTheme) {
        this.customTheme = customTheme;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleListLocation() {
        return moduleListLocation;
    }

    public void setModuleListLocation(String moduleListLocation) {
        this.moduleListLocation = moduleListLocation;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
