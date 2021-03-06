package ca.omny.auth.sessions;

import ca.omny.auth.token.AuthTokenParser;
import ca.omny.db.IDocumentQuerier;
import javax.servlet.http.HttpServletRequest;

public class SessionMapper {
    
    AuthTokenParser authTokenParser;
    
    IDocumentQuerier querier;
    
    public SessionMapper(IDocumentQuerier querier) {
        this.querier = querier;
        authTokenParser = new AuthTokenParser();
    }
    
    public String getUserId(HttpServletRequest request) {
        String token = authTokenParser.getToken(request);
        String key = querier.getKey("sessions",token);
        Session session = querier.get(key, Session.class);
        return session.getUserId();
    }

    public void setAuthTokenParser(AuthTokenParser authTokenParser) {
        this.authTokenParser = authTokenParser;
    }

    public void setQuerier(IDocumentQuerier querier) {
        this.querier = querier;
    }
}
