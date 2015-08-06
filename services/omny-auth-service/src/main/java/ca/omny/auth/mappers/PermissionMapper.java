package ca.omny.auth.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.auth.models.Permission;
import ca.omny.auth.models.Session;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PermissionMapper {

    public boolean hasPermission(Session session, String permission) {
        return this.hasPermission(session.getUserId().toString(), permission);
    }

    public boolean hasPermission(String userId, String permission) {
        return userId != null;
    }

    public Map<String, String> getPermissions(String userId, IDocumentQuerier database) {
        String prefix = database.getKey("permissions",userId);
        Collection<Permission> permissionList = database.getRange(prefix, Permission.class);
        Map<String, String> result = new HashMap<String, String>();
        for(Permission permission: permissionList) {
            result.put(permission.getPermission(), permission.getPermission());
        }
        return result;
    }
    
    public void grantPermission(String userId, String permission, String path, String host, IDocumentQuerier database) {
        Permission permissionObject = new Permission();
        permissionObject.setPermission(permission);
        permissionObject.setUserId(userId);
        grantPermission(permissionObject, host, database);
    }
    
    public void grantPermission(Permission permission, String host, IDocumentQuerier database) {
        permission.setPermission(host+"/"+permission.getPermission());
        String key = database.getKey("permissions",permission.getUserId(),permission.getPermission());
        database.set(key, permission);
    }
}
