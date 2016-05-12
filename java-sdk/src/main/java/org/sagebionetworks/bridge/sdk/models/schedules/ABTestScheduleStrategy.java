package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * A schedule selection strategy for schedule plans that randomly assigns study participants
 * into one of a set of groups, each apportioned a percentage of the total number of 
 * participants in the study. Because this assignment is truly random, the percentages may 
 * vary from the declared targets for smaller numbers of participants. 
 */
public final class ABTestScheduleStrategy implements ScheduleStrategy {

    List<ABTestGroup> groups = Lists.newArrayList();
    
    public void addGroup(int percentage, Schedule schedule) {
        checkArgument(percentage > 0);
        checkNotNull(schedule);
        addGroup(new ABTestGroup(percentage, schedule));
    }
    
    public void addGroup(ABTestGroup group) {
        groups.add(group);
    }
    
    public boolean removeGroup(ABTestGroup group) {
        return groups.remove(group);
    }
    
    public void setGroups(List<ABTestGroup> groups) {
        this.groups = groups;
    }
    
    @JsonProperty("scheduleGroups")
    public List<ABTestGroup> getGroups() {
        return this.groups;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(groups);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ABTestScheduleStrategy other = (ABTestScheduleStrategy) obj;
        return Objects.equals(groups, other.groups);
    }

    @Override
    public String toString() {
        return String.format("ABTestScheduleStrategy [groups=%s]", groups);
    }

}
