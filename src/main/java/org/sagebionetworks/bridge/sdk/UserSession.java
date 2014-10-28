package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class UserSession {

    private final String username;
    private final String sessionToken;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean dataSharing;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("dataSharing") boolean dataSharing) {
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.dataSharing = dataSharing;
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

    public boolean isDataSharing() {
        return this.dataSharing;
    }

    @Override
    public String toString() {
        return "UserSession[username=" + username + ", sessionToken=" + sessionToken + ", authenticated="
                + Boolean.toString(authenticated) + ", consented=" + Boolean.toString(consented) + ", dataSharing="
                + Boolean.toString(dataSharing) + "]";
    }

}
