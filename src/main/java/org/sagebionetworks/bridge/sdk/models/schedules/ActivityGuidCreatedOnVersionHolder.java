package org.sagebionetworks.bridge.sdk.models.schedules;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class ActivityGuidCreatedOnVersionHolder implements GuidCreatedOnVersionHolder {
    
    private final String guid;
    private final DateTime createdOn;
    
    @JsonCreator
    ActivityGuidCreatedOnVersionHolder(@JsonProperty("guid") String guid,
            @JsonProperty("createdOn") DateTime createdOn) {
        this.guid = guid;
        this.createdOn = createdOn;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public DateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public Long getVersion() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
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
        ActivityGuidCreatedOnVersionHolder other = (ActivityGuidCreatedOnVersionHolder) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return "ActivityGuidCreatedOnVersionHolder [guid=" + guid + ", createdOn=" + createdOn + "]";
    }
}
