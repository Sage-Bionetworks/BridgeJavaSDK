package org.sagebionetworks.bridge.scripts;

import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatePlans {
    
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    public static void main(String[] args) throws Exception {
        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAdminCredentials());

        ResearcherClient client = session.getResearcherClient();

        Schedule schedule = new Schedule();
        schedule.setLabel("Take the Quality of Life Survey");
        schedule.setActivityType(ActivityType.survey);
        schedule.setActivityRef("https://pd-staging.sagebridge.org/api/v1/surveys/e7e8b5c7-16b6-412d-bcf9-f67291781972/2014-09-25T20:07:50.794Z");
        SchedulePlan plan = new SchedulePlan();
        plan.setStrategy(new SimpleScheduleStrategy(schedule));
        client.createSchedulePlan(plan);
        
        schedule = new Schedule();
        schedule.setLabel("Take the Care Assessment Survey");
        schedule.setActivityType(ActivityType.survey);
        schedule.setActivityRef("https://pd-staging.sagebridge.org/api/v1/surveys/ecf7e761-c7e9-4bb6-b6e7-d6d15c53b209/2014-09-25T20:07:49.186Z");
        schedule.setCronTrigger("0 0 7 ? * MON,WED,FRI *");
        schedule.setExpires(Period.hours(6));
        plan = new SchedulePlan();
        
        ABTestScheduleStrategy strategy = new ABTestScheduleStrategy();
        strategy.addGroup(50, schedule);
        strategy.addGroup(50, schedule);
        plan.setStrategy(strategy);

        client.createSchedulePlan(plan);
        
        session.signOut();
    }

}
