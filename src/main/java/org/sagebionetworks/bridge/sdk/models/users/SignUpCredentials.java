package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

public final class SignUpCredentials {

    private String email;
    private String username;
    private String password;

    public SignUpCredentials(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(email);
        result = prime * result + Objects.hashCode(password);
        result = prime * result + Objects.hashCode(username);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SignUpCredentials other = (SignUpCredentials) obj;
        return (Objects.equals(email, other.email) && Objects.equals(password, other.password) && Objects.equals(
                username, other.username));
    }

    @Override
    public String toString() {
        return String.format("SignUpCredentials[email=%s, username=%s, password=[REDACTED]", email, username);
    }
}
