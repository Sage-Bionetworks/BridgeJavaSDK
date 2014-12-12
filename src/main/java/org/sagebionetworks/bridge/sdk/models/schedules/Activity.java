package org.sagebionetworks.bridge.sdk.models.schedules;

import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Activity {
    
    private final String label;
    private final ActivityType activityType;
    private final String ref;
    private final GuidCreatedOnVersionHolder survey;
    
    @JsonCreator
    public Activity(@JsonProperty("label") String label, @JsonProperty("activityType") ActivityType activityType,
            @JsonProperty("ref") String ref, @JsonProperty("survey") GuidCreatedOnVersionHolder survey) {
        this.label = label;
        this.activityType = activityType;
        this.ref = ref;
        this.survey = (survey == null) ? null : new ActivityGuidCreatedOnVersionHolder(survey.getGuid(), survey.getCreatedOn());
    }
    
    public Activity(String label, ActivityType activityType, String ref) {
        this.label = label;
        this.activityType = activityType;
        this.ref = ref;
        this.survey = null;
    }

    public String getLabel() {
        return label;
    }
    
    public ActivityType getActivityType() {
        return activityType;
    }

    public String getRef() {
        return ref;
    }

    public GuidCreatedOnVersionHolder getSurvey() {
        return survey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((survey == null) ? 0 : survey.hashCode());
        result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
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
        Activity other = (Activity) obj;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (survey == null) {
            if (other.survey != null)
                return false;
        } else if (!survey.equals(other.survey))
            return false;
        if (activityType != other.activityType)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Activity [activityType=" + activityType + ", ref=" + ref + ", survey=" + survey + "]";
    }

}
