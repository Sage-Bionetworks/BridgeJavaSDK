package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;

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
        this.activityType = SurveyReference.isSurveyRef(ref) ? ActivityType.SURVEY : ActivityType.TASK;
    }
    
    /**
     * Create an activity to do a survey.
     * @param label
     * @param survey
     */
    public Activity(String label, GuidCreatedOnVersionHolder survey) {
        this(label, ClientProvider.getConfig().getEnvironment().getUrl()
                + ClientProvider.getConfig().getSurveyApi(survey.getGuid(), survey.getCreatedOn()));
    }
    
    /**
     * Create an activity to do a survey (can be reference to a published survey with no specific
     * timestamp)l
     * @param label
     * @param reference
     */
    public Activity(String label, SurveyReference reference) {
        this(label, (reference.getCreatedOn() != null) ? ClientProvider.getConfig().getEnvironment().getUrl()
                + ClientProvider.getConfig().getSurveyApi(reference.getGuid(), reference.getCreatedOn())
                : ClientProvider.getConfig().getEnvironment().getUrl()
                        + ClientProvider.getConfig().getRecentlyPublishedSurveyUserApi(reference.getGuid()));
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
        return SurveyReference.isSurveyRef(ref) ? new SurveyReference(ref) : null;
    }
    
    @JsonIgnore
    public GuidCreatedOnVersionHolder getGuidCreatedOnVersionHolder() {
        final SurveyReference survey = getSurvey();
        if (survey != null && survey.getCreatedOn() != null) {
            return new SimpleGuidCreatedOnVersionHolder(survey.getGuid(), survey.getCreatedOn(), 0L);
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(activityType);
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(ref);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Activity other = (Activity) obj;
        return (Objects.equals(activityType, other.activityType) && Objects.equals(label, other.label) && Objects
                .equals(ref, other.ref));
    }

    @Override
    public String toString() {
        return String.format("Activity [activityType=%s, ref=%s]", activityType, ref);
    }

}
