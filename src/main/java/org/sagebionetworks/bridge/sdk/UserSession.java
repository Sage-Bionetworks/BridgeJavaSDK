package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class UserSession {

    private final String username;
    private final String sessionToken;
    private final SharingScope sharingScope;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean signedMostRecentConsent;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("signedMostRecentConsent") boolean signedMostRecentConsent) {
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.sharingScope = sharingScope;
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
    
    public SharingScope getSharingScope() {
        return this.sharingScope;
    }
    
    public boolean hasSignedMostRecentConsent() {
        return signedMostRecentConsent;
    }

    @Override
    public String toString() {
        return String.format("UserSession[username=%s, sessionToken=%s, authenticated=%s, consented=%s, sharingScope=%s, signedMostRecentConsent=%s]", 
                username, sessionToken, authenticated, consented, sharingScope, signedMostRecentConsent);
    }

}
