package org.sagebionetworks.bridge.sdk.models.schedules;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SchedulePlan.class)
public final class SchedulePlan implements GuidVersionHolder {

    private String guid;
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
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
        result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());
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
        SchedulePlan other = (SchedulePlan) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (modifiedOn == null) {
            if (other.modifiedOn != null)
                return false;
        } else if (!modifiedOn.equals(other.modifiedOn))
            return false;
        if (strategy == null) {
            if (other.strategy != null)
                return false;
        } else if (!strategy.equals(other.strategy))
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
        return "SchedulePlan [guid=" + guid + ", modifiedOn=" + modifiedOn + ", version=" + version + ", strategy="
                + strategy + "]";
    }

}
