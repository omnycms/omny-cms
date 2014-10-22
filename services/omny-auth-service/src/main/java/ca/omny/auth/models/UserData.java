package ca.omny.auth.models;

import java.util.Map;

public class UserData {
    public User user;
    public Map<String,String> permissions;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }
    
    
}
