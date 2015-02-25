package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class UserSession {

    private final String username;
    private final String sessionToken;
    private final ScopeOfSharing scopeOfSharing;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean signedMostRecentConsent;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("dataSharing") ScopeOfSharing scopeOfSharing,
            @JsonProperty("signedMostRecentConsent") boolean signedMostRecentConsent) {
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.scopeOfSharing = scopeOfSharing;
        this.signedMostRecentConsent = signedMostRecentConsent;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isConsented() {
        return this.consented;
    }

    public ScopeOfSharing getScopeOfSharing() {
        return this.scopeOfSharing;
    }
    
    public boolean hasSignedMostRecentConsent() {
        return signedMostRecentConsent;
    }

    @Override
    public String toString() {
        return String.format("UserSession[username=%s, sessionToken=%s, authenticated=%s, consented=%s, scopeOfSharing=%s, signedMostRecentConsent=%s]", 
                username, sessionToken, authenticated, consented, scopeOfSharing, signedMostRecentConsent);
    }

}
