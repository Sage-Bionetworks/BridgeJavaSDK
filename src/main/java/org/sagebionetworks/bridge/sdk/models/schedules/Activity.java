package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A directive to a participant to do a specific activity: either a task in the application, or completing
 * a run of a survey. 
 *
 */
public final class Activity {
    
    private final String label;
    private final String labelDetail;
    private final String guid;
    private final TaskReference task;
    private final SurveyReference survey;
    private final SurveyResponseReference response;
    private final ActivityType activityType;
    
    @JsonCreator
    private Activity(@JsonProperty("label") String label, @JsonProperty("labelDetail") String labelDetail, 
        @JsonProperty("guid") String guid, @JsonProperty("task") TaskReference task, 
        @JsonProperty("survey") SurveyReference survey, 
        @JsonProperty("surveyResponse") SurveyResponseReference response) {
        checkArgument(isNotBlank(label));
        checkArgument(task != null || survey != null);
        
        this.label = label;
        this.labelDetail = labelDetail;
        this.guid = guid;
        this.survey = survey;
        this.task = task;
        this.response = response;
        this.activityType = (task != null) ? ActivityType.TASK : ActivityType.SURVEY;
    }
    
    public Activity(String label, String labelDetail, TaskReference task) {
        this(label, labelDetail, null, task, null, null);
    }
    
    public Activity(String label, String labelDetail, SurveyReference survey) {
        this(label, labelDetail, null, null, survey, null);
    }
    
    /**
     * A label to show a participant in order to identify this activity in a user interface.  
     */
    public String getLabel() {
        return label;
    }
    /**
     * A label detail to show a participant in order to identify this activity in a user interface.  
     */
    public String getLabelDetail() {
        return labelDetail;
    }
    /**
     * The GUID for this activity.
     * @return
     */
    public String getGuid() {
        return guid;
    }
    /**
     * The type of this activity.
     */
    public ActivityType getActivityType() {
        return activityType;
    }
    /**
     * For survey activities, the key object for the survey referenced by the activity. This can be used 
     * through the SDK to retrieve the survey. 
     */
    public SurveyReference getSurvey() {
        return survey;
    }
    /**
     * For task activities, the key object indicating the task to be performed.
     */
    public TaskReference getTask() {
        return task;
    }
    /**
     * For survey activities, the key object for the survey response referenced by the activity. This can be used
     * through the SDK to submit survey answers and retrieve saved answers.
     */
    public SurveyResponseReference getSurveyResponse() {
        return response;
    }
    public boolean isPersistentlyRescheduledBy(Schedule schedule) {
        return schedule.schedulesImmediatelyAfterEvent() && getActivityFinishedEventId(schedule);
    }
    private boolean getActivityFinishedEventId(Schedule schedule) {
        String activityFinishedEventId = "activity:"+getGuid()+":finished";
        return schedule.getEventId().contains(getSelfFinishedEventId()) ||
               schedule.getEventId().contains(activityFinishedEventId);
    }
    private String getSelfFinishedEventId() {
        return (getActivityType() == ActivityType.SURVEY) ?
            ("survey:"+getSurvey().getGuid()+":finished") :
            ("task:"+getTask().getIdentifier()+":finished");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(activityType);
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(labelDetail);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(response);
        result = prime * result + Objects.hashCode(survey);
        result = prime * result + Objects.hashCode(task);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Activity other = (Activity) obj;
        return (Objects.equals(activityType, other.activityType) && 
            Objects.equals(label, other.label) && Objects.equals(labelDetail, other.labelDetail) &&
            Objects.equals(response, other.response) && Objects.equals(survey, other.survey) &&
            Objects.equals(guid, other.guid) && Objects.equals(task, other.task));
                        
    }

    @Override
    public String toString() {
        return String.format("Activity [label=%s, labelDetail=%s, guid=%s,task=%s, survey=%s, response=%s, activityType=%s]",
            label, labelDetail, guid, task, survey, response, activityType);
    }
}
