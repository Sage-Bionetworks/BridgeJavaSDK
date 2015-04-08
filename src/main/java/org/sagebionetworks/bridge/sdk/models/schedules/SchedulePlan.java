package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.Objects;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SchedulePlan.class)
public final class SchedulePlan implements GuidVersionHolder {

    private String guid;
    private String label;
    private DateTime modifiedOn;
    private Long version;
    private ScheduleStrategy strategy;

    @Override
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(modifiedOn);
        result = prime * result + Objects.hashCode(strategy);
        result = prime * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SchedulePlan other = (SchedulePlan) obj;
        return (Objects.equals(guid, other.guid) && Objects.equals(label, other.label) 
                && Objects.equals(modifiedOn, other.modifiedOn) && Objects.equals(strategy, other.strategy) 
                && Objects.equals(version, other.version));
    }

    @Override
    public String toString() {
        return String.format("SchedulePlan [guid=%s, label=%s, modifiedOn=%s, version=%s, strategy=%s]", 
                guid, label, modifiedOn, version, strategy);
    }

}
