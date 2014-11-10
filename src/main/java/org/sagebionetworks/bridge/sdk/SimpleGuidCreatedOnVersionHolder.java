package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class SimpleGuidCreatedOnVersionHolder implements GuidCreatedOnVersionHolder {

    private final String guid;
    private final DateTime createdOn;
    private final Long version;

    @JsonCreator
    SimpleGuidCreatedOnVersionHolder(@JsonProperty("guid") String guid,
            @JsonProperty("createdOn") DateTime createdOn, @JsonProperty("version") Long version) {
        this.guid = guid;
        this.createdOn = createdOn;
        this.version = version;
    }
    
    public String getGuid() {
        return guid;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }
    
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
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
        SimpleGuidCreatedOnVersionHolder other = (SimpleGuidCreatedOnVersionHolder) obj;
        if (createdOn == null) {
            if (other.createdOn != null)
                return false;
        } else if (!createdOn.equals(other.createdOn))
            return false;
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
        return "SimpleGuidCreatedOnVersionHolder [guid=" + guid + ", createdOn=" + createdOn + ", version=" + version
                + "]";
    }


}
