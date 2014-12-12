package org.sagebionetworks.bridge.sdk.models.schedules;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SchedulePlan.class) // strangely, Jackson needs this
public class SchedulePlan implements GuidVersionHolder {

    private String guid;
    private DateTime modifiedOn;
    private Long version;
    private ScheduleStrategy strategy;

    @JsonCreator
    private SchedulePlan(@JsonProperty("guid") String guid, @JsonProperty("modifiedOn") DateTime modifiedOn, @JsonProperty("version") Long version, @JsonProperty("strategy") ScheduleStrategy strategy) {
        this.guid = guid;
        this.modifiedOn = modifiedOn;
        this.version = version;
        this.strategy = strategy;
    }

    public SchedulePlan() {
    }

    @Override
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

    @Override
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
    
    /**
     * Set a schedule directly for a plan: all users in the study will get the same activities
     * on the same schedule. Equivalent to creating a SimpleScheduleStrategy with the same 
     * schedule, and adding that to the plan.
     * 
     * @param schedule
     */
    public void setSchedule(Schedule schedule) {
        setStrategy(new SimpleScheduleStrategy(schedule));
    }

    @Override
    public String toString() {
        return "SchedulePlan [guid=" + guid + ", modifiedOn=" + modifiedOn + ", version=" + version + ", strategy="
                + strategy + "]";
    }

}
