package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

class AdminSignUpCredentials {
    private final String username;
    private final String email;
    private final String password;
    private final List<String> roles;
    private final boolean consent;

    public AdminSignUpCredentials(SignUpCredentials signUp, List<String> roles, boolean consent) {
        this.username = signUp.getUsername();
        this.email = signUp.getEmail();
        this.password = signUp.getPassword();
        this.roles = roles;
        this.consent = consent;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public List<String> getRoles() {
        return roles;
    }
    public boolean isConsent() {
        return consent;
    }
    @Override
    public String toString() {
        return "AdminSignUpCredentials [username=" + username + ", email=" + email + ", password=" + password
                + ", roles=" + roles + ", consent=" + consent + "]";
    }
}


