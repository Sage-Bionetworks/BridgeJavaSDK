package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SimpleIdentifierHolder.class)
public interface IdentifierHolder {
    public String getIdentifier();
}
