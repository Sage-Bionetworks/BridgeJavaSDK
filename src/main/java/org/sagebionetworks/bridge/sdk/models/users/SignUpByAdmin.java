package org.sagebionetworks.bridge.sdk.models.users;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.sagebionetworks.bridge.sdk.Roles;

public class SignUpByAdmin {
    
    private final String email;
    private final String password;
    private final Set<Roles> roles;
    private final boolean consent;

    public SignUpByAdmin(String email, String password, Set<Roles> roles, boolean consent) {
        checkArgument(isNotBlank(email));
        checkArgument(isNotBlank(password));
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.consent = consent;
    }
    
    public String getEmail() {
        return email;
    }
    /**
     * NOTE: for migration support, this will be removed in early 2016.
     * @deprecated
     */
    public String getUsername() {
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


