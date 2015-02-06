package org.sagebionetworks.bridge.sdk;

import java.util.Set;

import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

class AdminSignUpCredentials {
    private final String username;
    private final String email;
    private final String password;
    private final Set<String> roles;
    private final boolean consent;

    public AdminSignUpCredentials(SignUpCredentials signUp, Set<String> roles, boolean consent) {
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
    public Set<String> getRoles() {
        return roles;
    }
    public boolean isConsent() {
        return consent;
    }
}


