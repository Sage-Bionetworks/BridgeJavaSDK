package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidVersionedOnHolder implements GuidVersionedOnHolder {

    private final String guid;
    private final DateTime versionedOn;

    @JsonCreator
    public SimpleGuidVersionedOnHolder(@JsonProperty("guid") String guid, @JsonProperty("versionedOn") DateTime versionedOn) {
        this.guid = guid;
        this.versionedOn = versionedOn;
    }
    
    public String getGuid() {
        return guid;
    }

    public DateTime getVersionedOn() {
        return versionedOn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((versionedOn == null) ? 0 : versionedOn.hashCode());
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
        SimpleGuidVersionedOnHolder other = (SimpleGuidVersionedOnHolder) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (versionedOn == null) {
            if (other.versionedOn != null)
                return false;
        } else if (!versionedOn.equals(other.versionedOn))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleGuidVersionedOnHolder [guid=" + guid + ", versionedOn=" + versionedOn + "]";
    }

}
