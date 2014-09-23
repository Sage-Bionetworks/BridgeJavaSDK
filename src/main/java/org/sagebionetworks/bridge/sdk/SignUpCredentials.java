package org.sagebionetworks.bridge.sdk;

import org.apache.commons.validator.routines.EmailValidator;

final class SignUpCredentials {

    private static final EmailValidator validator = EmailValidator.getInstance();

    private final String email;
    private final String username;
    private final String password;

    private SignUpCredentials(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    static SignUpCredentials valueOf(String email, String username, String password) {
        assert (validator.isValid(email));
        return new SignUpCredentials(email, username, password);
    }

    String getEmail() { return this.email; }
    String getUsername() { return this.username; }
    String getPassword() { return this.password; }

    @Override
    public String toString() {
        return "SignUpCredentials[email=" + email +
                ", username=" + username +
                ", password=" + password + "]";
    }
}
