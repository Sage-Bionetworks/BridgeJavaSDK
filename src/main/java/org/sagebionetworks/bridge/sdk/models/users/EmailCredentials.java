package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class EmailCredentials {

    private final String studyIdentifier;
    private final String email;
    
    public EmailCredentials(String studyIdentifier, String email) {
        this.studyIdentifier = studyIdentifier;
        this.email = email;
    }
    
    @JsonProperty("study")
    public String getStudyIdentifier() {
        return studyIdentifier;
    }
    
    public String getEmail() {
        return email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(email);
        result = prime * result + Objects.hashCode(studyIdentifier);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        EmailCredentials other = (EmailCredentials) obj;
        return Objects.equals(email, other.email) && Objects.equals(studyIdentifier, other.studyIdentifier);
    }

    @Override
    public String toString() {
        return String.format("EmailCredentials [studyIdentifier=%s, email=%s]", studyIdentifier, email);
    }
    
    
    
}
