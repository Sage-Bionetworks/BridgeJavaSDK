package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserSession {

    private final String email;
    private final String username;
    private final String sessionToken;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean dataSharing;

    @JsonCreator
    private UserSession(@JsonProperty("email") String email, @JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("dataSharing") boolean dataSharing) {
        this.email = email;
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.dataSharing = dataSharing;
    }

    public UserSession signOut() {
        return new UserSession(this.email, this.username, null, false, this.consented, this.dataSharing);
    }

    public String getEmail() { return this.email; }
    public String getUsername() { return this.username; }
    public String getSessionToken() { return this.sessionToken; }
    public boolean isAuthenticated() { return this.authenticated; }
    public boolean isConsented() { return this.consented; }
    public boolean isDataSharing() { return this.dataSharing; }

    @Override
    public String toString() {
        return "UserSession[username=" + username +
                ", sessionToken=" + sessionToken +
                ", authenticated=" + Boolean.toString(authenticated) +
                ", consented=" + Boolean.toString(consented) +
                ", dataSharing=" + Boolean.toString(dataSharing) + "]";
    }

}
