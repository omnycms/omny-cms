package ca.omny.auth.mappers;

import ca.omny.db.IDocumentQuerier;
import ca.omny.auth.models.Session;
import java.util.UUID;

public class SessionMapper {
    
    PermissionMapper permissionMapper = new PermissionMapper();
    UserMapper userMapper = new UserMapper();
    
    public Session getSession(String sessionId, IDocumentQuerier database) {
        String key = database.getKey("sessions",sessionId);
        Session session = database.get(key, Session.class);
       //session.setPermissions(permissionMapper.getPermissions(session.getUserId()));
        return session;
    }
    
    public void createSession(String userId, String sessionId, String sessionType, IDocumentQuerier database) {
        Session session = new Session();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setSessionType(sessionType);
        createSession(session, database);
    }
    
    public void createSession(Session session, IDocumentQuerier database) {
        String key = database.getKey("sessions",session.getSessionId());
        database.set(key, session);
    }
    
    public String createSession(String email, String type, IDocumentQuerier database) {
        String userId = userMapper.getIdFromEmail(email, database);
        if(userId==null) {
            userId = userMapper.createUser(email, database);
        }
        String sessionId = UUID.randomUUID().toString();
        this.createSession(userId, sessionId, type, database);
        
        return sessionId;
    }
}
