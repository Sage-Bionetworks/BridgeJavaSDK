package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleIdentifierHolder implements IdentifierHolder {

    private final String identifier;
    
    @JsonCreator
    SimpleIdentifierHolder(@JsonProperty("identifier") String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }

}
