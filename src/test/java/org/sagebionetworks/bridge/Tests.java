package org.sagebionetworks.bridge;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Config.Props;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.SurveyReference;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class Tests {

    public static void main(String[] args) {
        Config config = ClientProvider.getConfig();
        config.set(Props.HOST, "https://parkinson-staging.sagebridge.org");

        ResearcherClient client = ClientProvider.signIn(config.getAdminCredentials()).getResearcherClient();
        
        ResourceList<SchedulePlan> plans = client.getSchedulePlans();
        
        for (SchedulePlan plan : plans) {
            SimpleScheduleStrategy strategy = (SimpleScheduleStrategy)plan.getStrategy();
            
            SurveyReference ref = strategy.getSchedule().getActivities().get(0).getSurvey();
            
            System.out.println(ref);
            System.out.println(ref.getGuid());
            
            Survey survey = client.getSurveyMostRecentlyPublishedVersion(ref.getGuid());
            System.out.println(survey);
        }
    }
    
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
        Schedule schedule1 = new Schedule() {
            {
                setCronTrigger("* * *");
                setTaskActivity(this, "task:AAA");
                setExpires(Period.parse("PT60S"));
                setLabel("Test label for the user");
            }
        };
        Schedule schedule2 = new Schedule() {
            {
                setCronTrigger("* * *");
                setTaskActivity(this, "task:BBB");
                setExpires(Period.parse("PT60S"));
                setLabel("Test label for the user");
            }
        };
        Schedule schedule3 = new Schedule() {
            {
                setCronTrigger("* * *");
                setSurveyActivity(this, "http://host/surveys/GUID-AAA/2015-01-27T17:46:31.237Z");
                setExpires(Period.parse("PT60S"));
                setLabel("Test label for the user");
            }
        };
        ABTestScheduleStrategy strategy = new ABTestScheduleStrategy();
        strategy.addGroup(40, schedule1);
        strategy.addGroup(40, schedule2);
        strategy.addGroup(20, schedule3);
        plan.setStrategy(strategy);
        return plan;
    }
    
    public static SchedulePlan getSimpleSchedulePlan() {
        SchedulePlan plan = new SchedulePlan();
        Schedule schedule = new Schedule() {
            {
                setCronTrigger("* * *");
                setTaskActivity(this, "task:CCC");
                setExpires(Period.parse("PT60S"));
                setLabel("Test label for the user");
            }
        };
        plan.setSchedule(schedule);
        return plan;
    }

}
