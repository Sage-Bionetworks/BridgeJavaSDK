package org.sagebionetworks.bridge.sdk.models.studies;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class StudyConsent {

    private DateTime createdOn;
    private boolean active;
    private String path;
    private int minAge;
    private Long version;

    @JsonCreator
    private StudyConsent(@JsonProperty("timestamp") String createdOn, @JsonProperty("active") boolean active,
            @JsonProperty("path") String path, @JsonProperty("minAge") int minAge, @JsonProperty("version") Long version) {
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

    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(createdOn);
        result = prime * result + Objects.hashCode(path);
        result = prime * result + Objects.hashCode(version);
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + minAge;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        StudyConsent other = (StudyConsent) obj;
        return (Objects.equals(createdOn, other.createdOn) && Objects.equals(path, other.path)
                && Objects.equals(version, other.version) && active == other.active && minAge == other.minAge);
    }

    @Override
    public String toString() {
        return String.format("StudyConsent[createdOn=%s, active=%s, path=%s, minAge=%s, version=%s]", 
                createdOn.toString(ISODateTimeFormat.dateTime()), active, path, minAge, version);
    }
}
