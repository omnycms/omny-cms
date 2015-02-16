package ca.omny.extension.proxy;

import java.util.Collection;

public interface IPermissionCheck {

    boolean hasPermissions(String hostname, Collection<Permission> permissions, String token);
    
}
