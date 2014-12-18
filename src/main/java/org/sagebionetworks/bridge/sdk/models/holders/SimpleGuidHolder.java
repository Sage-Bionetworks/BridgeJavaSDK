package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidHolder implements GuidHolder {

    private final String guid;
    
    @JsonCreator
    SimpleGuidHolder(@JsonProperty("guid") String guid) {
        this.guid = guid;
    }
    
    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
        SimpleGuidHolder other = (SimpleGuidHolder) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleGuidHolder [guid=" + guid + "]";
    }

}
