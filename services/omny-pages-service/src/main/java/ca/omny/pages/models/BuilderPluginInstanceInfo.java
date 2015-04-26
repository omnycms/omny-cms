package ca.omny.pages.models;

public class BuilderPluginInstanceInfo {
    String module;
    String version;
    Object data;
    String omnyClass;
    String url;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOmnyClass() {
        return omnyClass;
    }

    public void setOmnyClass(String omnyClass) {
        this.omnyClass = omnyClass;
    }
    
    public Module getModuleClass() {
        Module module = new Module();
        module.setModule(this.module);
        module.setVersion(version);
        return module;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String toString() {
        return module+":"+version;
    }
}
