package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleIdentifierHolder implements IdentifierHolder {

    private final String identifier;
    
    @JsonCreator
    public SimpleIdentifierHolder(@JsonProperty("identifier") String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }

}
