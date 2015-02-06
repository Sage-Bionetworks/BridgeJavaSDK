package org.sagebionetworks.bridge.sdk.models.holders;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleIdentifierHolder implements IdentifierHolder {

    private final String identifier;
    
    @JsonCreator
    SimpleIdentifierHolder(@JsonProperty("identifier") String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(identifier);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SimpleIdentifierHolder other = (SimpleIdentifierHolder) obj;
        return Objects.equals(identifier, other.identifier);
    }

    @Override
    public String toString() {
        return String.format("SimpleIdentifierHolder [identifier=%s]", identifier);
    }

}
