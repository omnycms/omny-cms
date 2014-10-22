package ca.omny.auth.apis;

import ca.omny.auth.token.AuthTokenParser;
import com.google.gson.Gson;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.models.Permission;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import javax.inject.Inject;

public class Permissions implements OmnyApi {
    
    @Inject
    AuthTokenParser tokenParser;
    
    @Inject
    SessionMapper sessionMapper;
    
    @Inject
    PermissionMapper permissionMapper;
    
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
        permissionMapper.grantPermission(permission, requestResponseManager.getRequestHostname());
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
