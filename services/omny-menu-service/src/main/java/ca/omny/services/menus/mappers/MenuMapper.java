package ca.omny.services.menus.mappers;

import ca.omny.db.IDocumentQuerier;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import ca.omny.services.menus.models.Menu;
import ca.omny.services.menus.models.MenuEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MenuMapper {
    
    public Collection<String> getMenuNames(String host, IDocumentQuerier querier) {
        String key = querier.getKey("menus", host);
        Collection<Menu> menus = querier.getRange(key, Menu.class);
        LinkedList<String> result = new LinkedList<String>();
        for(Menu menu: menus) {
            result.add(menu.getMenuName());
        }
        return result;
    }
    
    public Collection<Menu> getFullMenus(String host, IDocumentQuerier querier) {
        String key = querier.getKey("menus", host);
        Collection<Menu> menus = querier.getRange(key, Menu.class);
        return menus;
    }
    
    public Menu getMenu(String host, String name, IDocumentQuerier querier) {
        String key = querier.getKey("menus", host, name);
        Menu menu = querier.get(key, Menu.class);
        return menu;
    }
    
    public void createMenu(String host,String menuName, IDocumentQuerier querier) {
        Menu menu = new Menu();
        menu.setMenuName(menuName);
        this.createMenu(host, menu, querier);
    }
    
    public void createMenu(String host, Menu menu, IDocumentQuerier querier) {
        String key = querier.getKey("menus", host, menu.getMenuName());
        querier.set(key, menu);
    }
    
    public void addMenuEntry(String host, String menuName, String title, String link, IDocumentQuerier querier) {
        Menu menu = this.getMenu(host, menuName, querier);
        
        if(menu.getMenuItems()==null) {
            menu.setMenuItems(new LinkedList<MenuEntry>());
        }
        
        MenuEntry menuEntry = this.findEntry(link, menu.getMenuItems());
        boolean adding = (menuEntry==null);
        if(adding) {
            menuEntry = new MenuEntry(); 
        }
        menuEntry.setSite(host);
        menuEntry.setMenu(menuName);
        menuEntry.setLink(link);
        menuEntry.setTitle(title);
        
        if(adding) {
            if(!link.substring(1).contains("/")) {
                menu.getMenuItems().add(menuEntry);
            } else {
                MenuEntry parent = this.findParent(link, menu.getMenuItems());
                if(parent==null) {
                    return;
                }
                if(parent.getChildren()==null) {
                    parent.setChildren(new LinkedList<MenuEntry>());
                }
                parent.getChildren().add(menuEntry);
            }
        }
        String key = querier.getKey("menus",host,menuName);
        querier.set(key, menu);
    }
    
    public MenuEntry findParent(String path, Collection<MenuEntry> entries) {
        String[] parts = path.substring(1).split("/");
        String[] parentParts = new String[parts.length-1];
        for(int i=0; i<parentParts.length; i++) {
            parentParts[i] = parts[i];
        }
        return findEntry(parentParts,0,entries);
    }
    
    public MenuEntry findEntry(String path, Collection<MenuEntry> entries) {
        String[] parts = path.substring(1).split("/");
        return findEntry(parts,0,entries);
    }
    
    public MenuEntry findEntry(String[] parts, int depth, Collection<MenuEntry> entries) {
        if(depth>=parts.length) {
            return null;
        }
        if(entries!=null) {
            for(MenuEntry entry: entries) {
                String[] entryParts = entry.getLink().substring(1).split("/");
                String current = entryParts[depth].substring(0,entryParts[depth].indexOf(".html"));
                String reference = parts[depth];
                if(depth==parts.length-1&&reference.endsWith(".html")) {
                    reference = reference.substring(0,reference.indexOf(".html"));
                }
                if(current.equals(reference)) {
                    if(depth==parts.length-1) {
                        return entry;
                    }
                    return findEntry(parts, depth+1, entry.getChildren());
                }
            }
        }
        
        return null;
    }
    
    public Collection<MenuEntry> getDefaultLinks(String hostname, StorageSystem storageSystem) {
        Gson gson = new Gson();
        String suffix = "Data.json";
        String prefix = "pages/current/";
        Collection<String> files = storageSystem.listFiles(prefix, true, suffix, hostname);
 
        Collection<String> fullPathFiles = new LinkedList<String>();
        for(String file: files) {
            fullPathFiles.add(prefix+file);
        }
        
        
        Collection<MenuEntry> menuEntries = storageSystem.fetchManyAsType(fullPathFiles, hostname, MenuEntry.class);
        Iterator<MenuEntry> iterator = menuEntries.iterator();
        for(String file: fullPathFiles) {
            MenuEntry menuEntry = iterator.next();
           
            menuEntry.setLink("/"+file.substring(prefix.length()).replace("Data.json", ".html"));
        }
        List<MenuEntry> entries = new LinkedList<MenuEntry>();
        entries.addAll(menuEntries);
        Collections.sort(entries, new MenuSorter());
        return this.turnFlatIntoStructured(entries);
    }
    
    public Collection<MenuEntry> turnFlatIntoStructured(Collection<MenuEntry> flat) {
        HashMap<String, MenuEntry> first = new HashMap<String, MenuEntry>();
        HashMap<String, MenuEntry> parents = new HashMap<String, MenuEntry>();
        HashMap<String, Collection<MenuEntry>> byDepth = new HashMap<String, Collection<MenuEntry>>();
        for(MenuEntry entry: flat) {
            String[] parts = entry.getLink().substring(1).split("/");
            if(parts.length==1) {
                first.put(parts[0], entry);
            } 
            parents.put(entry.getLink(), entry);
        }
        for(MenuEntry entry: flat) {
            String link = entry.getLink();
            String parentLink = link.substring(0,link.lastIndexOf("/"))+".html";
            MenuEntry parent = parents.get(parentLink);
            if(parent!=null) {
                if(parent.getChildren()==null) {
                    parent.setChildren(new LinkedList<MenuEntry>());
                }
                parent.getChildren().add(entry);
            }
        }
        Collection<MenuEntry> values = first.values();
        LinkedList<MenuEntry> entries = new LinkedList<MenuEntry>();
        entries.addAll(values);
        Collections.sort(entries,new MenuSorter());
        return entries;
    }
    
    public Collection<MenuEntry> getLinks(String host, String menuName, IDocumentQuerier querier) {
        Menu menu = this.getMenu(host, menuName, querier);
        return menu.getMenuItems();
    }
}
