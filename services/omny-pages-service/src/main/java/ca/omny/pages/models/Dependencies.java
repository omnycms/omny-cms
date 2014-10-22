package ca.omny.pages.models;

import java.util.Collection;

public class Dependencies {
    Collection<Module> dependencies;

    public Collection<Module> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Collection<Module> dependencies) {
        this.dependencies = dependencies;
    }
}
