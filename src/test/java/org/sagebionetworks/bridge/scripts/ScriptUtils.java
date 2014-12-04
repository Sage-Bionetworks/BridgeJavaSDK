package org.sagebionetworks.bridge.scripts;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;

import com.google.common.collect.Lists;

public class ScriptUtils {

    private static class ScheduleGuidCreatedOnHolder implements GuidCreatedOnVersionHolder {

        private final String guid;
        private final DateTime createdOn;
        
        ScheduleGuidCreatedOnHolder(String guid, DateTime timestamp) {
            this.guid = guid;
            this.createdOn = timestamp;
        }
        
        @Override
        public String getGuid() {
            return guid;
        }

        @Override
        public DateTime getCreatedOn() {
            return createdOn;
        }

        @Override
        public Long getVersion() {
            return null;
        }

    }

    /**
     * If you want to have a question that displays the choices "Yes" and "No", 
     * then it's not a boolean constraint because you have to specify the labels.
     * The return type can be the string "true"/"false" and therefore, a boolean.
     * @return
     */
    public static MultiValueConstraints booleanish() {
        return new MultiValueConstraints(DataType.BOOLEAN) {{
            setEnumeration(Lists.newArrayList(
                new SurveyQuestionOption("Yes", "true"),
                new SurveyQuestionOption("No", "false")
            ));
         }};
    }
    public static void setSurveyActivity(Schedule schedule, GuidCreatedOnVersionHolder keys) {
        checkNotNull(keys);
        
        String url = ClientProvider.getConfig().getHost()
                + ClientProvider.getConfig().getSurveyUserApi(keys.getGuid(), keys.getCreatedOn());
        schedule.setActivityType(ActivityType.survey);
        schedule.setActivityRef(url);
    }
    public static void setTaskActivity(Schedule schedule, String taskIdentifier) {
        checkNotNull(taskIdentifier);
        schedule.setActivityType(ActivityType.task);
        schedule.setActivityRef(taskIdentifier);
    }
    public static GuidCreatedOnVersionHolder getSurveyActivityKeys(Schedule schedule) {
        if (schedule != null && schedule.getActivityType() == ActivityType.survey && schedule.getActivityRef() != null) {
            String[] parts = schedule.getActivityRef().split("/surveys/")[1].split("/");
            final String guid = parts[0];
            final DateTime timestamp = DateTime.parse(parts[1]);
            return new ScriptUtils.ScheduleGuidCreatedOnHolder(guid, timestamp);
        }
        return null;
    }

}
