package ca.omny.auth.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.auth.models.Permission;
import ca.omny.auth.models.Session;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class PermissionMapper {

    @Inject
    IDocumentQuerier database;

    public boolean hasPermission(Session session, String permission) {
        return this.hasPermission(session.getUserId().toString(), permission);
    }

    public boolean hasPermission(String userId, String permission) {
        return userId != null;
    }

    public Map<String, String> getPermissions(String userId) {
        String prefix = database.getKey("permissions",userId);
        Collection<Permission> permissionList = database.getRange(prefix, Permission.class);
        Map<String, String> result = new HashMap<String, String>();
        for(Permission permission: permissionList) {
            result.put(permission.getPermission(), permission.getPermission());
        }
        return result;
    }
    
    public void grantPermission(String userId, String permission, String path, String host) {
        Permission permissionObject = new Permission();
        permissionObject.setPermission(permission);
        permissionObject.setUserId(userId);
        grantPermission(permissionObject, host);
    }
    
    public void grantPermission(Permission permission, String host) {
        permission.setPermission(host+"/"+permission.getPermission());
        String key = database.getKey("permissions",permission.getUserId(),permission.getPermission());
        database.set(key, permission);
    }
}
