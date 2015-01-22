package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public final class ABTestScheduleStrategy implements ScheduleStrategy {

    public static class Group {
        private final int percentage;
        private final Schedule schedule;
        
        @JsonCreator
        public Group(@JsonProperty("percentage") int percentage, @JsonProperty("schedule") Schedule schedule) {
            this.percentage = percentage;
            this.schedule = schedule;
        }
        public int getPercentage() {
            return percentage;
        }
        public Schedule getSchedule() {
            return schedule;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + percentage;
            result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
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
            Group other = (Group) obj;
            if (percentage != other.percentage)
                return false;
            if (schedule == null) {
                if (other.schedule != null)
                    return false;
            } else if (!schedule.equals(other.schedule))
                return false;
            return true;
        }
    }
    
    List<Group> groups = Lists.newArrayList();
    
    public void addGroup(int percentage, Schedule schedule) {
        checkArgument(percentage > 0);
        checkNotNull(schedule);
        addGroup(new Group(percentage, schedule));
    }
    
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    public boolean removeGroup(Group group) {
        return groups.remove(group);
    }
    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
    
    @JsonProperty("scheduleGroups")
    public List<Group> getGroups() {
        return this.groups;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groups == null) ? 0 : groups.hashCode());
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
        ABTestScheduleStrategy other = (ABTestScheduleStrategy) obj;
        if (groups == null) {
            if (other.groups != null)
                return false;
        } else if (!groups.equals(other.groups))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ABTestScheduleStrategy [groups=" + groups + "]";
    }

}
