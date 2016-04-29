package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * NOTE: This class will be removed once full support for persisting most fields in StudyParticipant 
 * at sign up is supported on the server.
 */
public final class SignUpCredentials {

    private String studyIdentifier;
    private String email;
    private String password;
    private Set<String> dataGroups;

    @JsonCreator
    public SignUpCredentials(@JsonProperty("study") String studyIdentifier, 
            @JsonProperty("email") String email, 
            @JsonProperty("password") String password, 
            @JsonProperty("dataGroups") Set<String> dataGroups) {
        this.studyIdentifier = studyIdentifier;
        this.email = email;
        this.password = password;
        this.dataGroups = dataGroups;
    }
    
    @JsonProperty("study")
    public String getStudyIdentifier() {
        return studyIdentifier;
    }

    public String getEmail() {
        return this.email;
    }
    
    /**
     * NOTE: for migration support, this will be removed in early 2016.
     * @deprecated
     */
    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return this.password;
    }
    
    public Set<String> getDataGroups() {
        return this.dataGroups;
    }

    public void setStudyIdentifier(String studyIdentifier) {
        this.studyIdentifier = studyIdentifier;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setDataGroups(Set<String> dataGroups) {
        this.dataGroups = dataGroups;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyIdentifier, email, password, dataGroups);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SignUpCredentials other = (SignUpCredentials) obj;
        return (Objects.equals(studyIdentifier, other.studyIdentifier) && Objects.equals(email, other.email)
                && Objects.equals(password, other.password) && Objects.equals(dataGroups, other.dataGroups));
    }

    @Override
    public String toString() {
        return String.format("SignUpCredentials[studyIdentifier=%s, email=%s, password=[REDACTED], dataGroups=%s",
                studyIdentifier, email, dataGroups);
    }
}
