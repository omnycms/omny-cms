package ca.omny.auth.apis;

import com.google.gson.Gson;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.models.Permission;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;

public class Permissions implements OmnyApi {
    
    PermissionMapper permissionMapper = new PermissionMapper();
    Gson gson;

    public Permissions() {
        gson = new Gson();
    }

    @Override
    public String getBasePath() {
        return AuthApiConstants.base+"/permissions";
    }

    @Override
    public String[] getVersions() {
        return AuthApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        Permission permission = requestResponseManager.getEntity(Permission.class);
        permissionMapper.grantPermission(permission, requestResponseManager.getRequestHostname(), requestResponseManager.getDatabaseQuerier());
        return new ApiResponse("", 201);
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
