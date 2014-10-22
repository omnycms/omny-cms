package ca.omny.auth.apis;

import com.google.gson.Gson;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.mappers.UserMapper;
import ca.omny.auth.models.Session;
import ca.omny.auth.token.AuthTokenParser;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import javax.inject.Inject;
import javax.ws.rs.QueryParam;

public class Users implements OmnyApi {
    
    @Inject
    AuthTokenParser tokenParser;
    
    @Inject
    UserMapper userMapper;
    
    @Inject
    SessionMapper sessionMapper;
    
    @Inject
    PermissionMapper permissionMapper;
    
    Gson gson;

    public Users() {
        gson = new Gson();
    }

    public String getUser(@QueryParam("sessionId") String sessionId) {
        Session session = sessionMapper.getSession(sessionId);
        if(session==null) {
            return gson.toJson(null);
        }
        return gson.toJson(userMapper.getUserData(session.getUserId()));
    }

    @Override
    public String getBasePath() {
        return AuthApiConstants.base+"/users";
    }

    @Override
    public String[] getVersions() {
        return AuthApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String sessionId = requestResponseManager.getQueryStringParameter("sessionId");
        Session session = sessionMapper.getSession(sessionId);
        if(session==null) {
            return new ApiResponse("", 404);
        }
        return new  ApiResponse(userMapper.getUserData(session.getUserId()), 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
