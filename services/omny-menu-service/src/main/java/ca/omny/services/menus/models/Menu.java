package ca.omny.services.menus.models;

import java.util.Collection;

public class Menu {
    String menuName;
    String rootPath;
    int depth;
    Collection<MenuEntry> menuItems;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Collection<MenuEntry> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Collection<MenuEntry> menuItems) {
        this.menuItems = menuItems;
    }

}
