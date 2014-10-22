package ca.omny.services.menus.models;

import java.util.Collection;

public class MenuEntry {
    String site;
    String menu;
    String link;
    String title;
    Collection<MenuEntry> children;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<MenuEntry> getChildren() {
        return children;
    }

    public void setChildren(Collection<MenuEntry> children) {
        this.children = children;
    }
}
