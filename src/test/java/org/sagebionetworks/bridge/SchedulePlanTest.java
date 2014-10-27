package org.sagebionetworks.bridge;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.BridgeResearcherClient;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;

public class SchedulePlanTest {
    
    public class TestABSchedulePlan extends SchedulePlan {
        private Schedule schedule1 = new Schedule() {{
            setCronTrigger("* * *");
            setActivityType(ActivityType.task);
            setActivityRef("task:AAA");
            setExpires(Period.parse("PT60S"));
            setLabel("Test label for the user");
        }};
        private Schedule schedule2 = new Schedule() {{
            setCronTrigger("* * *");
            setActivityType(ActivityType.task);
            setActivityRef("task:BBB");
            setExpires(Period.parse("PT60S"));
            setLabel("Test label for the user");
        }};
        private Schedule schedule3 = new Schedule() {{
            setCronTrigger("* * *");
            setActivityType(ActivityType.task);
            setActivityRef("task:CCC");
            setExpires(Period.parse("PT60S"));
            setLabel("Test label for the user");
        }};
        public TestABSchedulePlan() {
            ABTestScheduleStrategy strategy = new ABTestScheduleStrategy();
            strategy.addGroup(40, schedule1);
            strategy.addGroup(40, schedule2);
            strategy.addGroup(20, schedule3);
            setStrategy(strategy);
        }
    }
    
    public class TestSimpleSchedulePlan extends SchedulePlan {
        private Schedule schedule = new Schedule() {{
            setCronTrigger("* * *");
            setActivityType(ActivityType.task);
            setActivityRef("task:CCC");
            setExpires(Period.parse("PT60S"));
            setLabel("Test label for the user");
        }};
        public TestSimpleSchedulePlan() {
            SimpleScheduleStrategy strategy = new SimpleScheduleStrategy();
            strategy.setSchedule(schedule);
            setStrategy(strategy);
        }
    }

    private ClientProvider provider;
    private BridgeResearcherClient client;
    private GuidVersionHolder guidVersion;
    
    @Before
    public void before() {
        // Assumes the default user is a researcher in the API test (which I am, for now)
        provider = ClientProvider.valueOf();
        provider.signIn();
        
        client = provider.getResearcherClient();
    }
    
    @After
    public void after() {
        // Try and clean up if that didn't happen in the test.
        if (guidVersion != null) {
            client.deleteSchedulePlan(guidVersion.getGuid());
        }
        provider.signOut();
    }
    
    @Test
    public void normalUserCannotAccess() {
        /*
        UserSession session = null;
        try {
            session = helper.createUser(new SignUp("normal-test-user", "normal-test-user@sagebridge.org",
                    "P4ssword"), helper.getTestStudy(), true, true);
            
            SchedulePlan plan = new TestABSchedulePlan();
            String json = BridgeObjectMapper.get().writeValueAsString(plan);
            
            Response response = TestUtils.getURL(session.getSessionToken(), SCHEDULE_PLANS_URL).post(json).get(TIMEOUT);
            assertEquals("Returns 403", SC_FORBIDDEN, response.getStatus());
        } finally {
            helper.deleteUser(session);
        }*/
    }
    
    @Test
    public void crudSchedulePlan() throws Exception {
        SchedulePlan plan = new TestABSchedulePlan();
        
        // Create
        guidVersion = client.createSchedulePlan(plan);
        
        // Update
        plan = client.getSchedulePlan(guidVersion.getGuid());
        TestSimpleSchedulePlan simplePlan = new TestSimpleSchedulePlan();
        plan.setStrategy(simplePlan.getStrategy());
        
        GuidVersionHolder newGuidVersion = client.updateSchedulePlan(plan);
        assertNotEquals("Version should be updated", guidVersion.getVersion(), newGuidVersion.getVersion());

        // Get
        plan = client.getSchedulePlan(guidVersion.getGuid());
        assertEquals("Strategy type has been changed", "SimpleScheduleStrategy", plan.getStrategy().getClass().getSimpleName());
        
        // Verify schedules have been created 
        List<Schedule> schedules = provider.getClient().getSchedules();
        assertTrue("Schedules exist", !schedules.isEmpty());
        
        // Delete
        client.deleteSchedulePlan(guidVersion.getGuid());

        try {
            client.getSchedulePlan(guidVersion.getGuid());
            fail("Should have thrown an exception because plan was deleted");
        } catch(BridgeServerException e) {
            assertEquals("Returns not found message", "SchedulePlan not found.", e.getMessage());
            guidVersion = null;
        }
    }
    
    @Test(expected = BridgeServerException.class)
    @Ignore
    public void invalidPlanReturns400Error() {
        SchedulePlan plan = new TestABSchedulePlan();
        plan.setStrategy(null);
        
        // TODO: We'd like to examine the errors property of the JSON, add it to the InvalidEntityException.
        client.createSchedulePlan(plan);
        
        /*
        SchedulePlan plan = new TestABSchedulePlan();
        plan.setStrategy(null); // invalid
        String json = BridgeObjectMapper.get().writeValueAsString(plan);
        
        // Create
        Response response = TestUtils.getURL(session.getSessionToken(), SCHEDULE_PLANS_URL).post(json).get(TIMEOUT);
        assertEquals("Returns 400", SC_BAD_REQUEST, response.getStatus());
        
        JsonNode node = response.asJson();
        ArrayNode strategyErrors = (ArrayNode)node.get("errors").get("strategy");
        assertEquals("Has a strategy field error", 1, strategyErrors.size());
        */
    }
    
    @Test(expected = NullPointerException.class)
    @Ignore
    public void noPlanReturns400() {
        client.createSchedulePlan(null);
    }    
}
