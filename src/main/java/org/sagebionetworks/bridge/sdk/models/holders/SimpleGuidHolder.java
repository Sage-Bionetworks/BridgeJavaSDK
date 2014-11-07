package org.sagebionetworks.bridge.sdk.models.holders;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidHolder implements GuidHolder {

    private final String guid;

    @JsonCreator
    private SimpleGuidHolder(@JsonProperty("guid") String guid) {
        this.guid = guid;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public String toString() {
        return "SimpleGuidHolder [guid=" + guid + "]";
    }
}
