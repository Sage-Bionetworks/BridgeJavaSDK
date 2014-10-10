package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudyConsent {

    private static final ObjectMapper mapper = Utilities.getMapper();

    private final DateTime timestamp;
    private final boolean active;
    private final String path;
    private final int minAge;
    private final long version;

    @JsonCreator
    private StudyConsent(@JsonProperty("timestamp") String timestamp, @JsonProperty("active") boolean active,
            @JsonProperty("path") String path, @JsonProperty("minAge") int minAge, @JsonProperty("version") long version) {
        this.timestamp = timestamp == null ? null : DateTime.parse(timestamp, ISODateTimeFormat.dateTime());
        this.active = active;
        this.path = path;
        this.minAge = minAge;
        this.version = version;
    }

    // After creating this, will get junk values from getters. This is used exclusively to add a new StudyConsent.
    public static StudyConsent valueOf(String path, int minAge) {
        return new StudyConsent(null, false, path, minAge, -1);
    }

    public static StudyConsent valueOf(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json cannot be null.");
        }

        StudyConsent record = null;
        try {
            record = mapper.readValue(json, StudyConsent.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting JSON into IdVersionHolder: json="
                            + json, e);
        }
        return record;
    }

    public DateTime getTimestamp() { return timestamp; }
    public boolean isActive() { return active; }
    public String getPath() { return path; }
    public int getMinAge() { return minAge; }
    public long getVersion() { return version; }
}
