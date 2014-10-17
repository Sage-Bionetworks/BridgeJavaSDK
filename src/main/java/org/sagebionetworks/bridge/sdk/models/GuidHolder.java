package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GuidHolder {

    private final String guid;

    @JsonCreator
    private GuidHolder(@JsonProperty("guid") String guid) {
        this.guid = guid;
    }

    public String getGuid() { return guid; }

    @Override
    public String toString() {
        return "GuidHolder [guid=" + guid + "]";
    }
}
