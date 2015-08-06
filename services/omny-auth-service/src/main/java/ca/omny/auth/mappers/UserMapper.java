package ca.omny.auth.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.auth.models.User;
import ca.omny.auth.models.UserData;
import java.util.UUID;

public class UserMapper {
    
    PermissionMapper permissionMapper = new PermissionMapper();
    
    public UserData getUserData(String userId, IDocumentQuerier database) {
        String key = database.getKey("user",userId);
        User user = database.get(key, User.class);
        UserData userData = new UserData();
        userData.setUser(user);
        userData.setPermissions(permissionMapper.getPermissions(userId,database));
        return userData;
    }
    
    public String createUser(String email, IDocumentQuerier database) {
        String uid = UUID.randomUUID().toString();
        User user = new User();
        user.setUserId(uid);
        String key = database.getKey("emails",email);
        database.set(key, uid);
        
        key = database.getKey("user",uid);
        database.set(key, user);
        
        return uid;
    }
    
    public String getIdFromEmail(String email, IDocumentQuerier database) {
        String key = database.getKey("emails", email);
        return database.get(key, String.class);
    }
   
}
