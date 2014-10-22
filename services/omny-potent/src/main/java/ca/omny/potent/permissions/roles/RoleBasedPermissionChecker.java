package ca.omny.potent.permissions.roles;

import ca.omny.potent.models.AssignedRole;
import ca.omny.potent.models.Permission;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import javax.inject.Inject;

public class RoleBasedPermissionChecker implements IPermissionCheck {
    
    @Inject
    RoleMapper roleMapper;
    
    @Override
    public boolean hasPermissions(String hostname, Collection<Permission> permissions, String token) {
        Collection<AssignedRole> roles = roleMapper.getRoles(hostname,token);
        Collection<AssignedRole> appropriateRoles = this.getAppropriateRoles(permissions, roles);
        Collection<String> uniqueRoles = this.getUniqueRoles(appropriateRoles);
        return this.oneRoleHasPermission(uniqueRoles,permissions);
    }
    
    public boolean oneRoleHasPermission(Collection<String> roleIds, Collection<Permission> permissions) {
        if(roleIds.isEmpty()) {
            return false;
        }
        Collection<Map<String,Boolean>> rolePermissions = roleMapper.getRolePermissions(roleIds);
        for(Map<String,Boolean> permissionSet: rolePermissions) {
            boolean allPassed = true;
            for(Permission permission: permissions) {
                Boolean allowed = permissionSet.get(permission.getPermissionName());
                if(allowed==null||!allowed) {
                    allPassed = false;
                    break;
                }
            }
            if(allPassed) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<String> getUniqueRoles(Collection<AssignedRole> roles) {
        HashSet<String> finalRoles = new HashSet<String>();
        for(AssignedRole role: roles) {
            if(!finalRoles.contains(role.getRoleId())) {
                finalRoles.add(role.getRoleId());
            }
        }
        return finalRoles;
    }
    
    public Collection<AssignedRole> getAppropriateRoles(Collection<Permission> permissions, Collection<AssignedRole> roles) {
        Collection<AssignedRole> appropriateRoles = new LinkedList<AssignedRole>();
        for(AssignedRole role: roles) {
            boolean matches = true;
            for(Permission permission: permissions) {
                String rolePath = role.getBasePath();
                if(!rolePath.endsWith("/")&&rolePath.length()<permission.getResolvedPath().length()) {
                    rolePath += "/";
                }
                if(!permission.getResolvedPath().startsWith(rolePath)) {
                    matches=false;
                    break;
                }
            }
            if(matches) {
                appropriateRoles.add(role);
            }
        }
        return appropriateRoles;
    }

    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }
}
