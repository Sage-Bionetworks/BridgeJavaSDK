package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudyConsent {

    private static final ObjectMapper mapper = Utilities.getMapper();

    private final DateTime createdOn;
    private final boolean active;
    private final String path;
    private final int minAge;
    private final long version;

    @JsonCreator
    private StudyConsent(@JsonProperty("timestamp") String createdOn, @JsonProperty("active") boolean active,
            @JsonProperty("path") String path, @JsonProperty("minAge") int minAge, @JsonProperty("version") long version) {
        this.createdOn = createdOn == null ? null : DateTime.parse(createdOn, ISODateTimeFormat.dateTime());
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
                    "Something went wrong while converting JSON into StudyConsent: json="
                            + json, e);
        }
        return record;
    }

    public DateTime getCreatedOn() { return createdOn; }
    public boolean isActive() { return active; }
    public String getPath() { return path; }
    public int getMinAge() { return minAge; }
    public long getVersion() { return version; }

    @Override
    public String toString() {
        return "StudyConsent[createdOn=" + createdOn.toString(ISODateTimeFormat.dateTime()) +
                ", active=" + active +
                ", path=" + path +
                ", minAge=" + minAge +
                ", version=" + version + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        }
        final StudyConsent that = (StudyConsent) obj;
        return Objects.equals(this.createdOn, that.createdOn)
                && Objects.equals(this.active, that.active)
                && Objects.equals(this.path, that.path)
                && Objects.equals(this.minAge, that.minAge)
                && Objects.equals(this.version, that.version);
    }

}
