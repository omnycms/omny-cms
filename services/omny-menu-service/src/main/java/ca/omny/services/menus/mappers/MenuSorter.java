package ca.omny.services.menus.mappers;

import ca.omny.services.menus.models.MenuEntry;
import java.util.Comparator;

public class MenuSorter implements Comparator<MenuEntry>{

    public int compare(MenuEntry menuEntry1, MenuEntry menuEntry2) {
        String[] parts1 = menuEntry1.getLink().split("/");
        String[] parts2 = menuEntry2.getLink().split("/");
        
        if(parts1.length!=parts2.length) {
            return parts1.length-parts2.length;
        }
        
        String last1 = parts1[parts1.length-1];
        String last2 = parts2[parts2.length-1];
        if(last1.equals("default.html")) {
            return -1;
        }
        if(last2.equals("default.html")) {
            return 1;
        }
        return last1.compareTo(last2);
    }
    
}
