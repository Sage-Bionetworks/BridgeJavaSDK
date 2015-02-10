package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

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

    private String getAbsoluteUrl(String url) {
        return (url.startsWith("http")) ? url : ClientProvider.getConfig().getEnvironment().getUrl() + url;
    }
    
    @JsonCreator
    public Activity(@JsonProperty("label") String label, @JsonProperty("ref") String ref) {
        checkNotNull(label);
        checkNotNull(ref);

        this.label = label;
        if (SurveyReference.isSurveyRef(ref)) {
            this.ref = getAbsoluteUrl(ref);
            this.activityType = ActivityType.survey;
        } else {
            this.ref = ref;
            this.activityType = ActivityType.task;
        }
    }
    
    /**
     * Create an activity to do a survey.
     * @param label
     * @param activityType
     * @param ref
     */
    public Activity(String label, GuidCreatedOnVersionHolder survey) {
        checkNotNull(label);
        checkNotNull(survey);
        checkNotNull(survey.getGuid());
        checkNotNull(survey.getCreatedOn());
        
        String url = ClientProvider.getConfig().getSurveyUserApi(survey.getGuid(), survey.getCreatedOn());
        this.label = label;
        this.ref = getAbsoluteUrl(url);
        this.activityType = ActivityType.survey;
    }
    
    /**
     * Create an activity to do a survey, using a survey reference. If no createdOn is provided, the 
     * activity will point to the most recently published version of the survey.
     * @param label
     * @param activityType
     * @param ref
     */
    public Activity(String label, SurveyReference survey) {
        checkNotNull(label);
        checkNotNull(survey);
        checkNotNull(survey.getGuid());
        
        String url = (survey.getCreatedOn() != null) ?
                ClientProvider.getConfig().getSurveyUserApi(survey.getGuid(), DateTime.parse(survey.getCreatedOn())) :
                ClientProvider.getConfig().getRecentlyPublishedSurveyUserApi(survey.getGuid());
        this.label = label;
        this.ref = getAbsoluteUrl(url);
        this.activityType = ActivityType.survey;
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
        return String.format("Activity [label=%s, activityType=%s, ref=%s]", label, activityType, ref);
    }

}
