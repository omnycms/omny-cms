package ca.omny.potent.models;

public class OmnyEndpoint {
    String pattern;
    String name;
    String contextRoot;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }
}
