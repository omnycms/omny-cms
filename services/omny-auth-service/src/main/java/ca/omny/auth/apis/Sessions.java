package ca.omny.auth.apis;

import com.google.gson.Gson;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.models.Session;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;

public class Sessions implements OmnyApi {
    
    SessionMapper sessionMapper = new SessionMapper();
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
        Session session = sessionMapper.getSession(sessionId, requestResponseManager.getDatabaseQuerier());
        return new ApiResponse(session, 200);
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        
        Session session = requestResponseManager.getEntity(Session.class);
        
        sessionMapper.createSession(session, requestResponseManager.getDatabaseQuerier());
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
