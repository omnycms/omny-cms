package ca.omny.service.client;

import ca.omny.request.RequestResponseManager;
import ca.omny.service.client.models.User;
import ca.omny.service.client.models.UserData;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PermissionChecker {

    UserData userData;
    
    RequestResponseManager requestManager;
    
    String contextHostname;
    
    public PermissionChecker() {
       
    }
    
    public PermissionChecker(UserData userData) {
        this.userData = userData;
    }
    
    public void requirePermission(String type, String permission, String resource) {
        String hostname = this.getContextHostname();
        this.requirePermission(hostname, type, permission, resource);
    }
    
    public void requirePermission(String hostname, String type, String permission, String resource) {
        if(!this.hasPermission(hostname, type, permission, resource)) {
            //throw new NotPermittedException();
        }
    }
    
    public boolean hasPermission(String type, String permission, String resource) {
        return true;
    }

    public boolean hasPermission(String hostname, String type, String permission, String resource) {
        return true;
    }

    private boolean hasPermission(Map<String, String> permissions, String chain, LinkedList<String> remaining) {
        return true;
    }
    
    public String getContextHostname() {
        if(contextHostname==null) {
            contextHostname = requestManager.getRequestHostname();
        }
        return this.contextHostname;
    }

    public void setContextHostname(String contextHostname) {
        this.contextHostname = contextHostname;
    }
    public UserData getUserData() {
        if(userData==null) {
            this.setUserData();
        }
        return this.userData;
    }
    
    private void setUserData() {
        UserData data = new UserData();
        User user = new User();
        if(requestManager.getRequest()!=null) {
            user.setUserId(requestManager.getRequest().getHeader("X-UserId"));
        }
        data.setPermissions(new HashMap<String, String>());
        data.setUser(user);
        this.userData = data;
    }
    
}
