package org.sagebionetworks.bridge;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.Period;

import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.TaskReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Tests {
    // This seems like something that should be added to schedule.
    private static void setTaskActivity(Schedule schedule, String taskIdentifier) {
        checkNotNull(taskIdentifier);
        schedule.addActivity(new Activity("Task activity", null, new TaskReference(taskIdentifier)));
    }
    
    public static SchedulePlan getABTestSchedulePlan() {
        SchedulePlan plan = new SchedulePlan();
        plan.setMinAppVersion(2);
        plan.setMaxAppVersion(8);
        plan.setLabel("A/B Test Schedule Plan");
        Schedule schedule1 = new Schedule();
        schedule1.setCronTrigger("0 0 11 ? * MON,WED,FRI *");
        setTaskActivity(schedule1, "task:AAA");
        schedule1.setExpires(Period.parse("PT1H"));
        schedule1.setLabel("Test label for the user");
        
        Schedule schedule2 = new Schedule();
        schedule2.setCronTrigger("0 0 11 ? * MON,WED,FRI *");
        setTaskActivity(schedule2, "task:BBB");
        schedule2.setExpires(Period.parse("PT1H"));
        schedule2.setLabel("Test label for the user");

        Schedule schedule3 = new Schedule();
        schedule3.setCronTrigger("0 0 11 ? * MON,WED,FRI *");
        setTaskActivity(schedule3, "task:CCC");
        // This doesn't exist and now it matters, because we look for a survey to update the identifier
        // setSurveyActivity(schedule3, "identifier", "GUID-AAA", DateTime.parse("2015-01-27T17:46:31.237Z"));
        schedule3.setExpires(Period.parse("PT1H"));
        schedule3.setLabel("Test label for the user");

        ABTestScheduleStrategy strategy = new ABTestScheduleStrategy();
        strategy.addGroup(40, schedule1);
        strategy.addGroup(40, schedule2);
        strategy.addGroup(20, schedule3);
        plan.setStrategy(strategy);
        return plan;
    }

    public static Set<String> asStringSet(JsonNode parent, String property) {
        Set<String> results = new HashSet<>();
        if (parent != null && parent.hasNonNull(property)) {
            ArrayNode array = (ArrayNode)parent.get(property);
            for (int i = 0; i < array.size(); i++) {
                results.add(array.get(i).asText());
            }
        }
        return results;
    }
    
    public static String unescapeJson(String json) {
        return json.replaceAll("'", "\"");
    }
    
    /**
     * Guava does not have a version of this method that also lets you add items.
     */
    @SuppressWarnings("unchecked")
    public static <T> LinkedHashSet<T> newLinkedHashSet(T... items) {
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for (T item : items) {
            set.add(item);    
        }
        return set;
    }
    
}
