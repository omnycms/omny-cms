package ca.omny.auth.apis;

import com.google.gson.Gson;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.mappers.UserMapper;
import ca.omny.auth.models.Session;
import ca.omny.auth.token.AuthTokenParser;
import ca.omny.db.IDocumentQuerier;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
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

    public String getUser(@QueryParam("sessionId") String sessionId, IDocumentQuerier database) {
        Session session = sessionMapper.getSession(sessionId, database);
        if(session==null) {
            return gson.toJson(null);
        }
        return gson.toJson(userMapper.getUserData(session.getUserId(), database));
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
        Session session = sessionMapper.getSession(sessionId, requestResponseManager.getDatabaseQuerier());
        if(session==null) {
            return new ApiResponse("", 404);
        }
        return new  ApiResponse(userMapper.getUserData(session.getUserId(), requestResponseManager.getDatabaseQuerier()), 200);
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
