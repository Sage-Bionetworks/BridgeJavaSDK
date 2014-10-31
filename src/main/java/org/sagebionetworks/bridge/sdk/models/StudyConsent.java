package org.sagebionetworks.bridge.sdk.models;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StudyConsent {

    private DateTime createdOn;
    private boolean active;
    private String path;
    private int minAge;
    private long version;

    @JsonCreator
    private StudyConsent(@JsonProperty("timestamp") String createdOn, @JsonProperty("active") boolean active,
            @JsonProperty("path") String path, @JsonProperty("minAge") int minAge, @JsonProperty("version") long version) {
        this.createdOn = createdOn == null ? null : DateTime.parse(createdOn, ISODateTimeFormat.dateTime());
        this.active = active;
        this.path = path;
        this.minAge = minAge;
        this.version = version;
    }
    
    public StudyConsent() {
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public boolean isActive() {
        return active;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public int getMinAge() {
        return minAge;
    }
    
    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "StudyConsent[createdOn=" + createdOn.toString(ISODateTimeFormat.dateTime()) + ", active=" + active
                + ", path=" + path + ", minAge=" + minAge + ", version=" + version + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        }
        final StudyConsent that = (StudyConsent) obj;
        return Objects.equals(this.createdOn, that.createdOn) && Objects.equals(this.active, that.active)
                && Objects.equals(this.path, that.path) && Objects.equals(this.minAge, that.minAge)
                && Objects.equals(this.version, that.version);
    }

}
