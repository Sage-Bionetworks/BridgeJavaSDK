package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class SimpleVersionHolder implements VersionHolder {

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
