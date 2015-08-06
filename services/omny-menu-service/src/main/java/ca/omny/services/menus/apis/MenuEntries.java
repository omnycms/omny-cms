package ca.omny.services.menus.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.service.menus.helpers.MenuHelper;
import ca.omny.services.menus.mappers.MenuMapper;
import ca.omny.services.menus.models.MenuEntry;
import javax.inject.Inject;

public class MenuEntries implements OmnyApi {

    Gson gson;
    MenuMapper menuMapper;
    MenuHelper menuHelper;

    public MenuEntries() {
        gson = new Gson();
        menuMapper = new MenuMapper();
        menuHelper = new MenuHelper();
    }

    @Override
    public String getBasePath() {
        return MenuApiConstants.base+"/{menu}/entries";
    }

    @Override
    public String[] getVersions() {
        return MenuApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String menuName = requestResponseManager.getPathParameter("menu");
        if(menuName==null||menuName.equals("default")) {
            return new ApiResponse(menuMapper.getDefaultLinks(requestResponseManager.getRequestHostname(),requestResponseManager.getStorageSystem()),200);
        }
        return new ApiResponse(menuMapper.getLinks(requestResponseManager.getRequestHostname(), menuName, requestResponseManager.getDatabaseQuerier()),200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        MenuEntry entry = requestResponseManager.getEntity(MenuEntry.class);
        menuHelper.createEntryFromPage(requestResponseManager.getRequestHostname(), entry.getTitle(), entry.getLink(), requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 201);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

   
}
