package org.sagebionetworks.bridge.sdk.models.holders;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidVersionHolder implements GuidVersionHolder {

    private final String guid;
    private final Long version;

    @JsonCreator
    public SimpleGuidVersionHolder(@JsonProperty("guid") String guid, @JsonProperty("version") Long version) {
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
    public String toString() {
        return "SimpleGuidVersionHolder [guid=" + guid + ", version=" + version + "]";
    }

}
