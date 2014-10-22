package ca.omny.services.menus.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.services.menus.mappers.MenuMapper;
import ca.omny.services.menus.models.Menu;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public class Menus implements OmnyApi {

    Gson gson;
    
    @Inject
    RequestResponseManager requestResponseManager;
    
    @Inject
    MenuMapper menuMapper;
    
    @Context
    HttpServletRequest request;

    public Menus() {
        gson = new Gson();
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
        return new ApiResponse(menuMapper.getFullMenus(requestResponseManager.getRequestHostname()),200);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        Menu menu = requestResponseManager.getEntity(Menu.class);
        menuMapper.createMenu(requestResponseManager.getRequestHostname(), menu);
        return new ApiResponse("", 201);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
