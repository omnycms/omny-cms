package ca.omny.themes.models;

import java.util.Collection;

public class Theme {
    String name;
    String description;
    Collection<String> screenshots;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(Collection<String> screenshots) {
        this.screenshots = screenshots;
    }
}
