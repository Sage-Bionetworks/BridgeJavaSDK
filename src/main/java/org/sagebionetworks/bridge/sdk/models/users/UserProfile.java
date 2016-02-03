package org.sagebionetworks.bridge.sdk.models.users;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Sets;

public final class UserProfile {
    
    private static final Set<String> FIXED_PROPERTIES = Sets.newHashSet("firstName", "lastName", "email");
    
    private String firstName;
    private String lastName;
    private String email;
    private Map<String,String> attributes;

    public UserProfile() {
        attributes = new HashMap<String,String>();
    }
    
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void removeAttribute(String name) {
        if (isNotBlank(name)) {
            attributes.remove(name);
        }
    }
    @JsonAnySetter
    public void setAttribute(String name, String value) {
        checkArgument(!FIXED_PROPERTIES.contains(name), "Attribute '"+name+"' conflicts with existing Java field name.");
        if (isNotBlank(name) && isNotBlank(value)) {
            attributes.put(name, value);
        }
    }
    public String getAttribute(String name) {
        return attributes.get(name);
    }
    @JsonAnyGetter
    Map<String,String> getAttributes() {
        return attributes;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(email);
        result = prime * result + Objects.hashCode(firstName);
        result = prime * result + Objects.hashCode(lastName);
        result = prime * result + Objects.hashCode(attributes);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserProfile other = (UserProfile) obj;
        return (Objects.equals(email, other.email) && 
                Objects.equals(firstName, other.firstName) && 
                Objects.equals(lastName, other.lastName) && 
                Objects.equals(attributes, other.attributes));
    }
    @Override
    public String toString() {
        return String.format("UserProfile [firstName=%s, lastName=%s, email=%s, attributes=%s]", 
                firstName, lastName, email, attributes);
    }
}
