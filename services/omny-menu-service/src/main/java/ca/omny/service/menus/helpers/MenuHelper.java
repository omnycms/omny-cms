package ca.omny.service.menus.helpers;

import ca.omny.services.menus.mappers.MenuMapper;
import ca.omny.services.menus.models.Menu;
import java.util.Collection;
import javax.inject.Inject;

public class MenuHelper {
    
    @Inject
    MenuMapper menuMapper;
    
    public void createEntryFromPage(String host, String title, String page) {
        Collection<Menu> menus = menuMapper.getFullMenus(host);
        String path = "/"+page+".html";
        for(Menu menu: menus) {
            if(page.startsWith(menu.getRootPath())) {
                if(this.menuMatches(page, menu.getRootPath(), menu.getDepth())) {
                    menuMapper.addMenuEntry(host, menu.getMenuName(), title, path);
                    //find affected pages and update
                }
            }
        }
    }
 
    public boolean menuMatches(String path, String rootPath, int depth) {
        String[] parts = path.split("/");
        if(rootPath.isEmpty()) {
            return  parts.length <=depth;
        }
        String[] rootParts = rootPath.split("/");
        
        if(parts.length<rootParts.length) {
            return false;
        }
        
        for(int i=0; i<rootParts.length; i++) {
            if(!rootParts[i].equals(parts[i])) {
                return false;
            }
        }
        return parts.length-rootParts.length<=depth;
    }
}
