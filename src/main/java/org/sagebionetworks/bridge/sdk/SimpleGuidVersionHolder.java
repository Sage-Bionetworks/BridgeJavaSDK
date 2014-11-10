package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class SimpleGuidVersionHolder implements GuidVersionHolder {

    private final String guid;
    private final Long version;
    
    @JsonCreator
    SimpleGuidVersionHolder(@JsonProperty("guid") String guid, @JsonProperty("version") Long version) {
        this.guid = guid;
        this.version = version;
    }
    
    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
        SimpleGuidVersionHolder other = (SimpleGuidVersionHolder) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleGuidVersionHolder [guid=" + guid + ", version=" + version + "]";
    }

}
