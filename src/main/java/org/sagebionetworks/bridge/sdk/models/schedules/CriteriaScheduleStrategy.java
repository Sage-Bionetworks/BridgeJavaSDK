package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

/**
 * A schedule selection strategy for schedule plans that iterates through a list of schedules, 
 * matching each against a set of request criteria, returning the first schedule that matches 
 * the criteria. This can be used to change the schedules and/or tasks for each user. 
 */
public final class CriteriaScheduleStrategy implements ScheduleStrategy {

    private final List<ScheduleCriteria> scheduleCriteria = Lists.newArrayList();

    public void addCriteria(ScheduleCriteria criteria) {
        this.scheduleCriteria.add(criteria);
    }
    
    public List<ScheduleCriteria> getScheduleCriteria() {
        return scheduleCriteria;
    }
    
    public void setScheduleCriteria(List<ScheduleCriteria> criteria) {
        this.scheduleCriteria.clear();
        if (criteria != null) {
            this.scheduleCriteria.addAll(criteria);    
        }
    }    
    
    @Override
    public int hashCode() {
        return Objects.hash(scheduleCriteria);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CriteriaScheduleStrategy other = (CriteriaScheduleStrategy) obj;
        return Objects.equals(scheduleCriteria, other.scheduleCriteria);
    }

    @Override
    public String toString() {
        return "CriteriaScheduleStrategy [scheduleCriteria=" + scheduleCriteria + "]";
    }

}
