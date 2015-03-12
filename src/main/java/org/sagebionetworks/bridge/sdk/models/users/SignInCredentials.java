package org.sagebionetworks.bridge.sdk.models.users;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SignInCredentials {

    private String studyIdentifier;
    private String username;
    private String password;

    @JsonCreator
    public SignInCredentials(@JsonProperty("study") String studyIdentifier, @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        checkArgument(isNotBlank(studyIdentifier), "Study identifier cannot be blank/null");
        checkArgument(isNotBlank(username), "Username cannot be blank/null");
        checkArgument(isNotBlank(password), "Password cannot be blank/null");
        
        this.studyIdentifier = studyIdentifier;
        this.username = username;
        this.password = password;
    }

    @JsonProperty("study")
    public String getStudyIdentifier() {
        return studyIdentifier;
    }
    
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setStudyIdentifier(String studyIdentifier) {
        this.studyIdentifier = studyIdentifier;
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
        result = prime * result + Objects.hashCode(studyIdentifier);
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
        SignInCredentials other = (SignInCredentials) obj;
        return (Objects.equals(studyIdentifier, other.studyIdentifier) && Objects.equals(password, other.password) && Objects
                .equals(username, other.username));
    }

    @Override
    public String toString() {
        return String.format("SignInCredentials[study=%, username=%s, password=[REDACTED]", studyIdentifier, username);
    }
}
