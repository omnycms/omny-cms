package ca.omny.auth.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.auth.models.Session;
import java.util.UUID;
import javax.inject.Inject;

public class SessionMapper {
    
    @Inject
    IDocumentQuerier database;
    
    @Inject
    PermissionMapper permissionMapper;
    
    @Inject
    UserMapper userMapper;
    
    public Session getSession(String sessionId) {
        String key = database.getKey("sessions",sessionId);
        Session session = database.get(key, Session.class);
       //session.setPermissions(permissionMapper.getPermissions(session.getUserId()));
        return session;
    }
    
    public void createSession(String userId, String sessionId, String sessionType) {
        Session session = new Session();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setSessionType(sessionType);
        createSession(session);
    }
    
    public void createSession(Session session) {
        String key = database.getKey("sessions",session.getSessionId());
        database.set(key, session);
    }
    
    public String createSession(String email, String type) {
        String userId = userMapper.getIdFromEmail(email);
        if(userId==null) {
            userId = userMapper.createUser(email);
        }
        String sessionId = UUID.randomUUID().toString();
        this.createSession(userId, sessionId, type);
        
        return sessionId;
    }
}
