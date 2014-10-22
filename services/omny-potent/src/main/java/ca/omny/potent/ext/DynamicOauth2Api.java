package ca.omny.potent.ext;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

public class DynamicOauth2Api extends DefaultApi20 {

    String accessTokenEndPoint;
    String authorizationUrl;
    
    @Override
    public String getAccessTokenEndpoint() {
        return accessTokenEndPoint;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oac) {
        return authorizationUrl;
    }

    public void setAccessTokenEndPoint(String accessTokenEndPoint) {
        this.accessTokenEndPoint = accessTokenEndPoint;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }
    
}
