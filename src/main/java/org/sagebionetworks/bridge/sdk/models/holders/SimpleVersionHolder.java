package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleVersionHolder implements VersionHolder {

    private final Long version;
    
    @JsonCreator
    SimpleVersionHolder(@JsonProperty("version") Long version) {
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
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleVersionHolder other = (SimpleVersionHolder) obj;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

}
