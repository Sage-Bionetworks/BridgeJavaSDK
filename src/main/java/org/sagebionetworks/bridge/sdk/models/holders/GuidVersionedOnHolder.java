package org.sagebionetworks.bridge.sdk.models.holders;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuidVersionedOnHolder {

    private final String guid;
    private final DateTime versionedOn;

    @JsonCreator
    public GuidVersionedOnHolder(@JsonProperty("guid") String guid, @JsonProperty("versionedOn") DateTime versionedOn) {
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
    public String toString() {
        return "GuidVersionedOnHolder[guid=" + guid + ", versionedOn=" + versionedOn + "]";
    }

}
