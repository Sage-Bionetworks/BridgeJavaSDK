package org.sagebionetworks.bridge.sdk.models.holders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleVersionHolder implements VersionHolder {

    private final Long version;
    
    @JsonCreator
    public SimpleVersionHolder(@JsonProperty("version") Long version) {
        checkNotNull(version, "%s cannot be null", "version");
        this.version = version; 
    }
    
    @Override
    public Long getVersion() {
        return this.version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SimpleVersionHolder other = (SimpleVersionHolder) obj;
        return Objects.equals(version, other.version);
    }

    @Override
    public String toString() {
        return String.format("SimpleVersionHolder [version=%s]", version);
    }
}
