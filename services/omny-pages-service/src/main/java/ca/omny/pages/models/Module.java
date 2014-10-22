package ca.omny.pages.models;

import java.util.Collection;

public class Module {
    String type = "module";
    int schemaVersion = 1;
    String module;
    String version;
    

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
    
    public boolean equals(Object another) {
        Module d = (Module)another;
        return (d.getVersion().equals(version)&&d.getModule().equals(module));
    }
    
    public String toString() {
        return module+": "+version;
    }
}
