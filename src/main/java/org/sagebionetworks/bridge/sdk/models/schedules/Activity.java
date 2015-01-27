package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A directive to a participant to do a specific activity: either a task in the application, or completing
 * a run of a survey. 
 *
 */
public final class Activity {
    
    private final String label;
    private final ActivityType activityType;
    private final String ref;

    @JsonCreator
    public Activity(@JsonProperty("label") String label, @JsonProperty("ref") String ref) {
        checkNotNull(label);
        checkNotNull(ref);
        
        this.label = label;
        this.ref = ref;
        this.activityType = SurveyReference.isSurveyRef(ref) ? ActivityType.survey : ActivityType.task;
    }
    
    /**
     * Create an activity to do a survey.
     * @param label
     * @param activityType
     * @param ref
     */
    public Activity(String label, GuidCreatedOnVersionHolder survey) {
        this(checkNotNull(label), checkNotNull(ClientProvider.getConfig().getSurveyUserApi(survey.getGuid(), survey.getCreatedOn())));
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
    public SurveyReference getSurvey() {
        return ref.contains("/surveys/") ? new SurveyReference(ref) : null;
    }
    
    @JsonIgnore
    public GuidCreatedOnVersionHolder getGuidCreatedOnVersionHolder() {
        final SurveyReference survey = getSurvey();
        if (survey != null && survey.getCreatedOn() != null) {
            return new GuidCreatedOnVersionHolder() {
                @Override public String getGuid() {
                    return survey.getGuid();
                }
                @Override public DateTime getCreatedOn() {
                    return DateTime.parse(survey.getCreatedOn());
                }
                @Override public Long getVersion() {
                    return null;
                }
            };
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
        if (activityType != other.activityType)
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Activity [activityType=" + activityType + ", ref=" + ref + "]";
    }

}
