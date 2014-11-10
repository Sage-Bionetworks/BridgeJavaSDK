package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SimpleGuidVersionHolder.class)
public interface GuidVersionHolder {
    public String getGuid();
    public Long getVersion();
}
