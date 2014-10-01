package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class UserProfile {

    private static final Utilities utils = Utilities.getInstance();
    private static final ObjectMapper mapper = Utilities.getMapper();

    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @JsonCreator
    private UserProfile(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
            @JsonProperty("username") String username, @JsonProperty("email") String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    public static UserProfile valueOf(String json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON cannot be null.");
        }
        UserProfile profile = null;
        try {
            profile = mapper.readValue(json, UserProfile.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public static UserProfile valueOf() {
        return new UserProfile(null, null, null, null);
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public UserProfile setFirstName(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("firstName cannot be null.");
        }
        this.firstName = firstName;
        return this;
    }

    public UserProfile setLastName(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("lastName cannot be null.");
        }
        this.lastName = lastName;
        return this;
    }

    public UserProfile setUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null.");
        }
        this.username = username;
        return this;
    }

    public UserProfile setEmail(String email) {
        if (!utils.isValidEmail(email)) {
            throw new IllegalArgumentException("Email is not valid: " + email);
        }
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "UserProfile[firstName=" + firstName + ", "
                + "lastName=" + lastName + ", "
                + "username=" + username + ", "
                + "email=" + email + "]";
    }

}
