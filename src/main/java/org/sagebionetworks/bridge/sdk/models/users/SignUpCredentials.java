package org.sagebionetworks.bridge.sdk.models.users;


public class SignUpCredentials {

    private String email;
    private String username;
    private String password;

    public SignUpCredentials(String email, String username, String password) {
        this.email = email;
        this.username = username;
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
    public String toString() {
        return "SignUpCredentials[email=" + email + ", username=" + username + ", password=[REDACTED]";
    }
}
