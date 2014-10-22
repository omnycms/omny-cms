package com.omny.credentials;

import ca.omny.documentdb.IDocumentQuerier;
import java.util.Map;
import javax.inject.Inject;

public class CredentialManager {

    @Inject
    IDocumentQuerier querier;
    
    static final String CREDENTIAL_PREFIX = "credentials::";

    public CredentialManager() {

    }

    public Map getCredentials(String key) {
        return this.getCredentials(key, Map.class);
    }

    public <T extends Object> T getCredentials(String key, Class<T> c) {
        return querier.get(CREDENTIAL_PREFIX + key, c);
    }

    public void setCredentials(String key, Object credentials) {
        querier.set(CREDENTIAL_PREFIX + key, credentials);
    }
}
