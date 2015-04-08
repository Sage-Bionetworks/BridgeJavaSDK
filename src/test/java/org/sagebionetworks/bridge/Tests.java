package org.sagebionetworks.bridge;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

public class Tests {
    
    public static final String TEST_KEY = "api";
    
    public static final String ADMIN_ROLE = "admin";

    public static final String RESEARCHER_ROLE = TEST_KEY + "_researcher";

    public static final Properties getApplicationProperties() {
        Properties properties = new Properties();
        File file = new File("src/main/resources/bridge-sdk.properties");
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
    
    public static final String randomIdentifier() {
        return ("sdk-" + RandomStringUtils.randomAlphabetic(5)).toLowerCase();
    }
    
    // This seems like something that should be added to schedule.
    private static void setTaskActivity(Schedule schedule, String taskIdentifier) {
        checkNotNull(taskIdentifier);
        schedule.addActivity(new Activity("Task activity", taskIdentifier));
    }
    
    private static void setSurveyActivity(Schedule schedule, String url) {
        checkNotNull(url);
        schedule.addActivity(new Activity("Survey activity", url));
    }
    
    public static SchedulePlan getABTestSchedulePlan() {
        SchedulePlan plan = new SchedulePlan();
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
        setSurveyActivity(schedule3, "http://host/surveys/GUID-AAA/2015-01-27T17:46:31.237Z");
        schedule3.setExpires(Period.parse("PT1H"));
        schedule3.setLabel("Test label for the user");

        ABTestScheduleStrategy strategy = new ABTestScheduleStrategy();
        strategy.addGroup(40, schedule1);
        strategy.addGroup(40, schedule2);
        strategy.addGroup(20, schedule3);
        plan.setStrategy(strategy);
        return plan;
    }
    
    public static SchedulePlan getSimpleSchedulePlan() {
        SchedulePlan plan = new SchedulePlan();
        plan.setLabel("Cron-based schedule");
        Schedule schedule = new Schedule();
        schedule.setCronTrigger("0 0 11 ? * MON,WED,FRI *");
        setTaskActivity(schedule, "task:CCC");
        schedule.setExpires(Period.parse("PT1H"));
        schedule.setLabel("Test label for the user");

        plan.setSchedule(schedule);
        return plan;
    }

}
