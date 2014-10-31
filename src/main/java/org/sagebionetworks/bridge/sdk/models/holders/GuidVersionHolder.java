package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuidVersionHolder {
    
    private final String guid;
    private final Long version;
    
    @JsonCreator
    public GuidVersionHolder(@JsonProperty("guid") String guid, @JsonProperty("version") Long version) {
        this.guid = guid;
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "GuidVersionHolder [guid=" + guid + ", version=" + version + "]";
    }

}
