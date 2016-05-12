package org.sagebionetworks.bridge.sdk.models.holders;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SimpleVersionHolder.class)
public interface VersionHolder {
    public Long getVersion();
}
