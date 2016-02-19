package ca.omny.auth.helpers;

import ca.omny.auth.token.AuthTokenParser;
import ca.omny.auth.mappers.PermissionMapper;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.models.Session;
import ca.omny.db.IDocumentQuerier;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class PermissionHelper {
    
    AuthTokenParser authTokenParser = new AuthTokenParser();
    SessionMapper sessionMapper = new SessionMapper();
    PermissionMapper permissionMapper = new PermissionMapper();
    
    public void grantPermission(HttpServletRequest request, String userId, String permission, String path, String host, IDocumentQuerier database) throws IllegalAccessException {
        String token = authTokenParser.getToken(request);
        Session session = sessionMapper.getSession(token, database);
        Map<String, String> permissions = permissionMapper.getPermissions(session.getUserId(), database);
        
        //check permission of user
        if(!permissions.containsKey("/*")) {
            throw new IllegalAccessException("Does not have permission to grant permissions");
        }
        
        permissionMapper.grantPermission(userId, permission, path, host, database);
    }
}
