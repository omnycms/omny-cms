package ca.omny.auth.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.auth.models.User;
import ca.omny.auth.models.UserData;
import java.util.UUID;
import javax.inject.Inject;

public class UserMapper {
    
    @Inject
    IDocumentQuerier database;
    
    @Inject
    PermissionMapper permissionMapper;
    
    public UserData getUserData(String userId) {
        String key = database.getKey("user",userId);
        User user = database.get(key, User.class);
        UserData userData = new UserData();
        userData.setUser(user);
        userData.setPermissions(permissionMapper.getPermissions(userId));
        return userData;
    }
    
    public String createUser(String email) {
        String uid = UUID.randomUUID().toString();
        User user = new User();
        user.setUserId(uid);
        String key = database.getKey("emails",email);
        database.set(key, uid);
        
        key = database.getKey("user",uid);
        database.set(key, user);
        
        return uid;
    }
    
    public String getIdFromEmail(String email) {
        String key = database.getKey("emails", email);
        return database.get(key, String.class);
    }
   
}
