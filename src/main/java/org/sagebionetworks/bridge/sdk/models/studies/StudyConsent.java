package org.sagebionetworks.bridge.sdk.models.studies;

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
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + minAge;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StudyConsent other = (StudyConsent) obj;
        if (active != other.active)
            return false;
        if (createdOn == null) {
            if (other.createdOn != null)
                return false;
        } else if (!createdOn.equals(other.createdOn))
            return false;
        if (minAge != other.minAge)
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StudyConsent[createdOn=" + createdOn.toString(ISODateTimeFormat.dateTime()) + ", active=" + active
                + ", path=" + path + ", minAge=" + minAge + ", version=" + version + "]";
    }

}
