package ca.omny.services.menus.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.services.menus.mappers.MenuMapper;
import ca.omny.services.menus.models.Menu;

public class Menus implements OmnyApi {

    Gson gson;
    
    MenuMapper menuMapper;

    public Menus() {
        gson = new Gson();
        menuMapper = new MenuMapper();
    }


    @Override
    public String getBasePath() {
        return MenuApiConstants.base;
    }

    @Override
    public String[] getVersions() {
        return MenuApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        return new ApiResponse(menuMapper.getFullMenus(requestResponseManager.getRequestHostname(), requestResponseManager.getDatabaseQuerier()),200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        Menu menu = requestResponseManager.getEntity(Menu.class);
        menuMapper.createMenu(requestResponseManager.getRequestHostname(), menu, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 201);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
