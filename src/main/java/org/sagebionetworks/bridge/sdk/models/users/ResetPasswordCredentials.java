package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

public final class ResetPasswordCredentials {

    private final String email;
    
    public ResetPasswordCredentials(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return this.email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(email);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ResetPasswordCredentials other = (ResetPasswordCredentials) obj;
        return Objects.equals(email, other.email);
    }

    @Override
    public String toString() {
        return String.format("ResetPasswordCredentials [email=%s]", email);
    }
    
}
