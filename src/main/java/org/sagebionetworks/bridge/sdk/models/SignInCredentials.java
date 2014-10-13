package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInCredentials {

    private String username;
    private String password;

    @JsonCreator
    private SignInCredentials(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public static SignInCredentials valueOf() {
        return new SignInCredentials(null, null);
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    public SignInCredentials setUsername(String username) {
        this.username = username;
        return this;
    }

    public SignInCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "SignInCredentials[username=" + username +
                ", password=" + password + "]";
    }
}
