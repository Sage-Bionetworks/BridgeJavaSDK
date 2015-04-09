package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

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
            result = prime * result + Objects.hashCode(schedule);
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Group other = (Group) obj;
            return (percentage == other.percentage && Objects.equals(schedule, other.schedule));
        }
        @Override
        public String toString() {
            return String.format("ABTestScheduleStrategy.Group [percentage=%s, schedule=%s]", percentage, schedule);
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
