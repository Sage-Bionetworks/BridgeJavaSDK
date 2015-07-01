package org.sagebionetworks.bridge.sdk.models.users;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.sagebionetworks.bridge.sdk.Roles;

public class SignUpByAdmin {
    
    private final String username;
    private final String email;
    private final String password;
    private final Set<Roles> roles;
    private final boolean consent;

    public SignUpByAdmin(String username, String email, String password, Set<Roles> roles, boolean consent) {
        checkArgument(isNotBlank(username));
        checkArgument(isNotBlank(email));
        checkArgument(isNotBlank(password));
        this.username = username;
        this.email = email;
        this.password = password;
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
    public Set<Roles> getRoles() {
        return roles;
    }
    public boolean isConsent() {
        return consent;
    }
}


