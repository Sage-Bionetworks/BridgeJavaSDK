package org.sagebionetworks.bridge.sdk.models.schedules;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SchedulePlan {

    private String guid;
    private DateTime modifiedOn;
    private Long version;
    private ScheduleStrategy strategy;
    
    @JsonCreator
    public SchedulePlan(@JsonProperty("guid") String guid, @JsonProperty("modifiedOn") DateTime modifiedOn, @JsonProperty("version") Long version, @JsonProperty("strategy") ScheduleStrategy strategy) {
        this.guid = guid;
        this.modifiedOn = modifiedOn;
        this.version = version;
        this.strategy = strategy;
    }
    
    public SchedulePlan() {
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public DateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(DateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public ScheduleStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ScheduleStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "SchedulePlan [guid=" + guid + ", modifiedOn=" + modifiedOn + ", version=" + version + ", strategy="
                + strategy + "]";
    }
    
}
