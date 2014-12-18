package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleVersionHolder implements VersionHolder {

    private final Long version;
    
    @JsonCreator
    SimpleVersionHolder(@JsonProperty("version") Long version) {
        this.version = version; 
    }
    
    @Override
    public Long getVersion() {
        return this.version;
    }

}
