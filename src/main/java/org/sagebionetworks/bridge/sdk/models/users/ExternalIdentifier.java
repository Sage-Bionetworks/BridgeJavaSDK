package org.sagebionetworks.bridge.sdk.models.users;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

public final class ExternalIdentifier {
    
    private final String identifier;
    
    public ExternalIdentifier(String identifier) {
        checkArgument(isNotBlank(identifier), "identifier cannot be null, an empty string or whitespace");
        this.identifier = identifier;
    }
    
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
        ExternalIdentifier other = (ExternalIdentifier) obj;
        return Objects.equals(identifier, other.identifier);
    }

    @Override
    public String toString() {
        return String.format("ExternalIdentifier[identifier=%s", identifier);
    }
}
