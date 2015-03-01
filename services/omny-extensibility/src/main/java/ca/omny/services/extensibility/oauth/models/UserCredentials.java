package ca.omny.services.extensibility.oauth.models;

import ca.omny.extension.proxy.AccessRule;
import java.util.Collection;

public class UserCredentials {
    String accessToken;
    String refreshToken;
    
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
