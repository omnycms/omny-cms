package ca.omny.auth.apis;

import ca.omny.auth.token.AuthTokenParser;
import com.google.gson.Gson;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.models.Session;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import javax.inject.Inject;

public class Sessions implements OmnyApi {
    
    @Inject
    AuthTokenParser tokenParser;
    
    @Inject
    SessionMapper sessionMapper;
    
    @Inject
    PermissionMapper permissionMapper;
    
    Gson gson;

    public Sessions() {
        gson = new Gson();
    }

    @Override
    public String getBasePath() {
        return AuthApiConstants.base+"/sessions";
    }

    @Override
    public String[] getVersions() {
        return AuthApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String sessionId = requestResponseManager.getQueryStringParameter("sessionId");
        Session session = sessionMapper.getSession(sessionId);
        return new ApiResponse(session, 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        
        Session session = requestResponseManager.getEntity(Session.class);
        
        sessionMapper.createSession(session);
        return new ApiResponse("", 200);
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
