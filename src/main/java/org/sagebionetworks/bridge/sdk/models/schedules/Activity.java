package org.sagebionetworks.bridge.sdk.models.schedules;

import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A directive to a participant to do a specific activity: either a task in the application, or completing
 * a run of a survey. 
 *
 */
public class Activity {
    
    private final String label;
    private final ActivityType activityType;
    private final String ref;
    private final GuidCreatedOnVersionHolder survey;
    
    // Constructor for de-serialization of Activity. Survey property is supplied by the server but 
    // does not need to be set on the client.
    @JsonCreator
    private Activity(@JsonProperty("label") String label, @JsonProperty("activityType") ActivityType activityType,
            @JsonProperty("ref") String ref, @JsonProperty("survey") GuidCreatedOnVersionHolder keys) {
        this.label = label;
        this.activityType = activityType;
        this.ref = ref;
        this.survey = keys;
    }
    
    /**
     * Create an activity to do a task.
     * @param label
     * @param activityType
     * @param ref
     */
    public Activity(String label, ActivityType activityType, String ref) {
        this(label, activityType, ref, null);
    }

    /**
     * A label to show a participant in order to identify this activity in a user interface.  
     */
    public String getLabel() {
        return label;
    }
    /**
     * The type of this activity.
     */
    public ActivityType getActivityType() {
        return activityType;
    }
    /**
     * The string reference identifier for the activity, which varies based on the activity type. 
     * for tasks, this will be a unique identifier for the task; for surveys, this will be a link 
     * to retrieve the survey via the Bridge API.
     */
    public String getRef() {
        return ref;
    }
    /**
     * For survey tasks, the key object for the survey referenced by the activity. This can be used 
     * through the SDK to retrieve the survey. 
     */
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
