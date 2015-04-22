package ca.omny.extension.proxy;

import java.util.Collection;

public class AccessRule {
    public static final String UNAUTHENTICATED = "unauthenticated";
    public static final String AUTHENTICATED = "authenticated";
    public static final String OWNER = "owner";
    public static final String RESTRICTED = "restricted";
    
    String authorizationLevel;
    String pattern;
    String method;
    Collection<Permission> permissions;

    public String getAuthorizationLevel() {
        return authorizationLevel;
    }

    public void setAuthorizationLevel(String authorizationLevel) {
        this.authorizationLevel = authorizationLevel;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
