package org.sagebionetworks.bridge.sdk.models.users;

public class ResetPasswordCredentials {

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
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResetPasswordCredentials other = (ResetPasswordCredentials) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResetPasswordCredentials [email=" + email + "]";
    }
    
}
