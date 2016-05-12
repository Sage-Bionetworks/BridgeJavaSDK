package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SimpleGuidHolder.class)
public interface GuidHolder {
    public String getGuid();
}
