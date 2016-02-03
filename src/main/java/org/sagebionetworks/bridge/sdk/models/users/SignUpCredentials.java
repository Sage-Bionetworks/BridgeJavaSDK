package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

import org.sagebionetworks.bridge.sdk.json.DataGroupsDeserializer;
import org.sagebionetworks.bridge.sdk.json.DataGroupsSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class SignUpCredentials {

    private String studyIdentifier;
    private String email;
    private String password;
    @JsonDeserialize(using=DataGroupsDeserializer.class)
    @JsonSerialize(using=DataGroupsSerializer.class)
    private DataGroups dataGroups;

    @JsonCreator
    public SignUpCredentials(@JsonProperty("study") String studyIdentifier, 
            @JsonProperty("email") String email, 
            @JsonProperty("password") String password, 
            @JsonProperty("dataGroups") DataGroups dataGroups) {
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
    
    public DataGroups getDataGroups() {
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
    
    public void setDataGroups(DataGroups dataGroups) {
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
