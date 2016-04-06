package org.sagebionetworks.bridge.sdk.models.users;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ExternalIdentifier {
    
    private final String identifier;
    private final boolean isAssigned;

    public ExternalIdentifier(String identifier) {
        checkArgument(isNotBlank(identifier), "identifier cannot be null, an empty string or whitespace");
        this.identifier = identifier;
        this.isAssigned = false;
    }
    
    @JsonCreator
    ExternalIdentifier(@JsonProperty("identifier") String identifier,
            @JsonProperty("assigned") boolean isAssigned) {
        checkArgument(isNotBlank(identifier), "identifier cannot be null, an empty string or whitespace");

        this.identifier = identifier;
        this.isAssigned = isAssigned;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public boolean isAssigned() {
        return isAssigned;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(identifier, isAssigned);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ExternalIdentifier other = (ExternalIdentifier) obj;
        return Objects.equals(identifier, other.identifier) && Objects.equals(isAssigned, other.isAssigned);
    }

    @Override
    public String toString() {
        return String.format("ExternalIdentifier[identifier=%s", identifier);
    }
}
