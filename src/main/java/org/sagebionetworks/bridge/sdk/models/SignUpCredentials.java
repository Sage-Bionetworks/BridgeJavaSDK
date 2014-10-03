package org.sagebionetworks.bridge.sdk.models;

import org.apache.commons.validator.routines.EmailValidator;

public class SignUpCredentials {

    private static final EmailValidator validator = EmailValidator.getInstance();

    private String email;
    private String username;
    private String password;

    private SignUpCredentials(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static SignUpCredentials valueOf(String email, String username, String password) {
        assert validator.isValid(email);
        return new SignUpCredentials(email, username, password);
    }

    public static SignUpCredentials valueOf() {
        return new SignUpCredentials(null, null, null);
    }

    public String getEmail() { return this.email; }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    public SignUpCredentials setEmail(String email) {
        this.email = email;
        return this;
    }
    public SignUpCredentials setUsername(String username) {
        this.username = username;
        return this;
    }
    public SignUpCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "SignUpCredentials[email=" + email +
                ", username=" + username +
                ", password=" + password + "]";
    }
}
