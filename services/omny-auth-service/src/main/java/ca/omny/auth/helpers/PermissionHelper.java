package ca.omny.auth.helpers;

import ca.omny.auth.token.AuthTokenParser;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.models.Session;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class PermissionHelper {
    
    @Inject
    AuthTokenParser authTokenParser;
    
    @Inject
    SessionMapper sessionMapper;
    
    @Inject
    PermissionMapper permissionMapper;
    
    public void grantPermission(HttpServletRequest request, String userId, String permission, String path, String host) throws IllegalAccessException {
        String token = authTokenParser.getToken(request);
        Session session = sessionMapper.getSession(token);
        Map<String, String> permissions = permissionMapper.getPermissions(session.getUserId());
        
        //check permission of user
        if(!permissions.containsKey("/*")) {
            throw new IllegalAccessException("Does not have permission to grant permissions");
        }
        
        permissionMapper.grantPermission(userId, permission, path, host);
    }
}
