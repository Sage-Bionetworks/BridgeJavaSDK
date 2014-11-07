package org.sagebionetworks.bridge.sdk.models.holders;

import org.joda.time.DateTime;

public interface GuidVersionedOnHolder {
    public String getGuid();
    public DateTime getVersionedOn();
}
