package ca.omny.potent.permissions.roles;

import ca.omny.potent.models.Permission;
import java.util.Collection;

public interface IPermissionCheck {

    boolean hasPermissions(String hostname, Collection<Permission> permissions, String token);
    
}
