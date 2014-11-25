package org.sagebionetworks.bridge.sdk.models.users;

public class ResetPasswordCredentials {

    private final String email;
    
    public ResetPasswordCredentials(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return this.email;
    }
    
}
