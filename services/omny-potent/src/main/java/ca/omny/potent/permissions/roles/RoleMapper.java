package ca.omny.potent.permissions.roles;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.models.AssignedRole;
import ca.omny.potent.models.Permission;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import javax.inject.Inject;

public class RoleMapper {

    @Inject
    IDocumentQuerier querier;

    public boolean roleHasAllPermissions(String roleId, Collection<Permission> permissions) {
        LinkedList<String> keys = new LinkedList<String>();
        for (Permission permission : permissions) {
            String key = querier.getKey("role_permissions", roleId);
            keys.add(key);
        }
        Collection<Map> result = querier.multiGetCollection(keys, Map.class);
        return result.size() == permissions.size();
    }

    public Collection<Map<String, Boolean>> getRolePermissions(Collection<String> roleIds) {
        LinkedList<String> keys = new LinkedList<String>();
        for (String roleId : roleIds) {
            String key = querier.getKey("role_permissions", roleId);
            keys.add(key);
        }
        Collection<Map<String, Boolean>> result = new LinkedList<Map<String, Boolean>>();
        Collection<Map> maps = querier.multiGetCollection(keys, Map.class);
        for (Map map : maps) {
            result.add((Map<String, Boolean>) map);
        }
        return result;
    }

    public Collection<AssignedRole> getRoles(String hostname, String token) {
        String uid = this.getUid(token);
        String key = querier.getKey("roles", hostname, uid);
        Collection<AssignedRole> roles = querier.getRange(key, AssignedRole.class);
        return roles;
    }

    public String getUid(String token) {
        String key = querier.getKey("sessions", token);
        Map session = querier.get(key, Map.class);
        if (session != null && session.containsKey("userId")) {
            return session.get("userId").toString();
        }
        return null;
    }

}
