package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class SignInCredentials {

    private String username;
    private String password;

    @JsonCreator
    private SignInCredentials(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    static SignInCredentials valueOf() {
        return new SignInCredentials(null, null);
    }

    static SignInCredentials valueOf(String username, String password) {
        return new SignInCredentials(username, password);
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    SignInCredentials setUsername(String username) {
        this.username = username;
        return this;
    }

    SignInCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "SignInCredentials[username=" + username +
                ", password=" + password + "]";
    }
}
