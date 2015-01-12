package org.sagebionetworks.bridge.sdk.models.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserProfile {

    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;

    @JsonCreator
    private UserProfile(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
            @JsonProperty("username") String username, @JsonProperty("email") String email,
            @JsonProperty("phone") String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }
    
    public static UserProfile valueOf() {
        return new UserProfile(null, null, null, null, null);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }

    public UserProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserProfile setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UserProfile setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String toString() {
        return "UserProfile[firstName=" + firstName + ", " + "lastName=" + lastName + ", " + "username=" + username
                + ", " + "phone=" + phone + ", " + "email=" + email + "]";
    }

}
