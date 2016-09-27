package org.sagebionetworks.bridge.sdk.models.holders;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SimpleGuidCreatedOnVersionHolder.class)
public interface GuidCreatedOnVersionHolder {
    String getGuid();
    DateTime getCreatedOn();
    Long getVersion();
}
