package ca.omny.auth.apis;

import ca.omny.db.IDocumentQuerier;
import com.google.gson.Gson;
import ca.omny.auth.mappers.SessionMapper;
import ca.omny.auth.mappers.UserMapper;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class GoogleLogin implements OmnyApi {

    protected static Map<String, Class> acceptedProviders;

    static Logger logger = Logger.getLogger(GoogleLogin.class.getName());

    IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
    UserMapper userMapper = new UserMapper();
    SessionMapper sessionMapper = new SessionMapper();
    
    @Override
    public String getBasePath() {
        return AuthApiConstants.base+"/login/google";
    }

    @Override
    public String[] getVersions() {
        return AuthApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        try {
            String oauthToken = requestResponseManager.getQueryStringParameter("oauth_token");
            UUID uuid = UUID.randomUUID();
            
            String getState = URLEncoder.encode(requestResponseManager.getRequest().getQueryString(), "UTF-8");
            String key = querier.getKey("credentials","oauth","google");
            Map credentials = querier.get(key, Map.class);
            
            ServiceBuilder builder = new ServiceBuilder()
                    .provider(Google2Api.class)
                    .apiKey((String) credentials.get("key"))
                    .apiSecret((String) credentials.get("secret"));

            if (credentials.containsKey("scope")) {
                builder.scope(credentials.get("scope").toString());
            }
            OAuthService service = builder.build();
            
            Verifier verifier = new Verifier(oauthToken);
            
            Token accessToken = new Token(oauthToken, credentials.get("secret").toString());
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo");
            service.signRequest(accessToken, request);
            Response response = request.send();
            Gson g = new Gson();
            Map responseMap = g.fromJson(response.getBody(), Map.class);
            String email = responseMap.get("email").toString();
            logger.info(uuid.toString() + " email from google " + email);
            //get user by email
            String sessionId = sessionMapper.createSession(email, "oauth", querier);
            
            return new ApiResponse(sessionId, 200);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GoogleLogin.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiResponse(500, 500);
        }
        
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
