package ca.omny.potent.ext.models;

import ca.omny.potent.ext.DynamicOauth2Api;
import java.util.Map;

public class ServiceCredentials {
    String clientId;
    String secret;
    String refreshWebMethod;
    String apiPrefix;
    Map<String,String> refreshParameters;
    String refreshTokenUrl;
    DynamicOauth2Api apiConfig;
    String signatureType;
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Map<String, String> getRefreshParameters() {
        return refreshParameters;
    }

    public void setRefreshParameters(Map<String, String> refreshParameters) {
        this.refreshParameters = refreshParameters;
    }

    public String getRefreshTokenUrl() {
        return refreshTokenUrl;
    }

    public void setRefreshTokenUrl(String refreshTokenUrl) {
        this.refreshTokenUrl = refreshTokenUrl;
    }

    public DynamicOauth2Api getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(DynamicOauth2Api apiConfig) {
        this.apiConfig = apiConfig;
    }

    public String getRefreshWebMethod() {
        return refreshWebMethod;
    }

    public void setRefreshWebMethod(String refreshWebMethod) {
        this.refreshWebMethod = refreshWebMethod;
    }

    public String getApiPrefix() {
        return apiPrefix;
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    public String getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }
}
