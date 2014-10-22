package ca.omny.service.client.models;

import java.util.Map;

public class UserData {
    private User user;
    private Map<String,String> permissions;

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
