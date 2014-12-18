package org.sagebionetworks.bridge.scripts;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;

import com.google.common.collect.Lists;

public class ScriptUtils {

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
    public static void setMostRecentlyPublishedSurveyActivity(Schedule schedule, String guid) {
        checkNotNull(schedule);
        checkNotNull(guid);
        
        Config config = ClientProvider.getConfig();
        String url = config.getHost() + config.getRecentlyPublishedSurveyUserApi(guid);
        
        schedule.addActivity(new Activity("Take survey", ActivityType.survey, url));
    }
    public static void setSurveyActivity(Schedule schedule, GuidCreatedOnVersionHolder keys) {
        checkNotNull(schedule);
        checkNotNull(keys);

        Config config = ClientProvider.getConfig();
        String url = config.getHost() + config.getSurveyUserApi(keys.getGuid(), keys.getCreatedOn());
        
        schedule.addActivity(new Activity("Take survey", ActivityType.survey, url));
    }
    public static void setTaskActivity(Schedule schedule, String taskIdentifier) {
        checkNotNull(taskIdentifier);
        schedule.addActivity(new Activity("Take task", ActivityType.task, taskIdentifier));
    }
    public static GuidCreatedOnVersionHolder getSurveyActivityKeys(Schedule schedule) {
        return schedule.getActivities().get(0).getSurvey();
    }

}
