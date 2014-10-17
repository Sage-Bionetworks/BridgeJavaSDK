package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuidVersionedOnHolder {

    private final String guid;
    private final DateTime version;

    @JsonCreator
    public GuidVersionedOnHolder(@JsonProperty("guid") String guid, @JsonProperty("versionedOn") DateTime version) {
        this.guid = guid;
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public DateTime getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "GuidVersionedOnHolder[guid=" + guid + ", version=" + version + "]";
    }

}
