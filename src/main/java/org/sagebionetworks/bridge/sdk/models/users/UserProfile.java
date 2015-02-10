package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

public final class UserProfile {
    
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(email);
        result = prime * result + Objects.hashCode(firstName);
        result = prime * result + Objects.hashCode(lastName);
        result = prime * result + Objects.hashCode(phone);
        result = prime * result + Objects.hashCode(username);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserProfile other = (UserProfile) obj;
        return (Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
                && Objects.equals(lastName, other.lastName) && Objects.equals(phone, other.phone) && Objects.equals(
                username, other.username));
    }
    @Override
    public String toString() {
        return String.format("UserProfile [firstName=%s, lastName=%s, username=%s, phone=%s, email=%s]", 
                firstName, lastName, username, phone, email);
    }
}
