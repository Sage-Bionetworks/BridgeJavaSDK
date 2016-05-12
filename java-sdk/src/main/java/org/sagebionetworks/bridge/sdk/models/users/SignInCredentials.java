package org.sagebionetworks.bridge.sdk.models.users;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SignInCredentials {

    private String studyIdentifier;
    private String email;
    private String password;

    @JsonCreator
    public SignInCredentials(@JsonProperty("study") String studyIdentifier, @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        checkArgument(isNotBlank(studyIdentifier), "Study identifier cannot be blank/null");
        checkArgument(isNotBlank(email), "Email cannot be blank/null");
        checkArgument(isNotBlank(password), "Password cannot be blank/null");
        
        this.studyIdentifier = studyIdentifier;
        this.email = email;
        this.password = password;
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
        return this.email;
    }

    public String getPassword() {
        return this.password;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(studyIdentifier);
        result = prime * result + Objects.hashCode(password);
        result = prime * result + Objects.hashCode(email);
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
                .equals(email, other.email));
    }

    @Override
    public String toString() {
        return String.format("SignInCredentials[study=%, email=%s, password=[REDACTED]", studyIdentifier, email);
    }
}
