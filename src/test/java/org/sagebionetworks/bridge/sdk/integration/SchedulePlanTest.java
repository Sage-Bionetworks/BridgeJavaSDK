package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.sagebionetworks.bridge.Tests.untilConsistent;

import java.util.List;
import java.util.concurrent.Callable;

import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;

public class SchedulePlanTest {

    public static class TestABSchedulePlan extends SchedulePlan {
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

    private GuidVersionHolder guidVersion;
    
    private TestUser user;
    private TestUser researcher;
    private ResearcherClient researcherClient;
    private UserClient userClient;

    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);
        researcher = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true, "teststudy_researcher");
        
        researcherClient = researcher.getSession().getResearcherClient();
        userClient = user.getSession().getUserClient();
    }

    @After
    public void after() {
        // Try and clean up if that didn't happen in the test.
        if (guidVersion != null) {
            researcherClient.deleteSchedulePlan(guidVersion.getGuid());
        }
        researcher.signOutAndDeleteUser();
        user.signOutAndDeleteUser();
    }
    
    @Test
    public void normalUserCannotAccess() {
        TestUser user = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);
        try {
            
            SchedulePlan plan = new TestABSchedulePlan();
            user.getSession().getResearcherClient().createSchedulePlan(plan);
            fail("Should have returned Forbidden status");
            
        } catch(BridgeServerException e) {
            assertEquals("Non-researcher gets 403 forbidden", 403, e.getStatusCode());
        } finally {
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void crudSchedulePlan() throws Exception {
        SchedulePlan plan = new TestABSchedulePlan();
        
        // Create
        guidVersion = researcherClient.createSchedulePlan(plan);

        // Update
        plan = researcherClient.getSchedulePlan(guidVersion.getGuid());
        TestSimpleSchedulePlan simplePlan = new TestSimpleSchedulePlan();
        plan.setStrategy(simplePlan.getStrategy());

        GuidVersionHolder newGuidVersion = researcherClient.updateSchedulePlan(plan);
        assertNotEquals("Version should be updated", guidVersion.getVersion(), newGuidVersion.getVersion());

        // Get
        plan = researcherClient.getSchedulePlan(guidVersion.getGuid());
        assertEquals("Strategy type has been changed", "SimpleScheduleStrategy", plan.getStrategy().getClass().getSimpleName());

        untilConsistent(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return (!userClient.getSchedules().isEmpty());
            }
        });
        
        List<Schedule> schedules = userClient.getSchedules();
        assertTrue("Schedules exist", !schedules.isEmpty());

        // Delete
        researcherClient.deleteSchedulePlan(guidVersion.getGuid());

        try {
            researcherClient.getSchedulePlan(guidVersion.getGuid());
            fail("Should have thrown an exception because plan was deleted");
        } catch(BridgeServerException e) {
            assertEquals("Returns 404 Not Found", 404, e.getStatusCode());
            guidVersion = null;
        }
    }
    
    @Test
    public void invalidPlanReturns400Error() {
        try {
            
            SchedulePlan plan = new TestABSchedulePlan();
            plan.setStrategy(null);
            researcherClient.createSchedulePlan(plan);
            fail("Plan was invalid and should have thrown an exception");
            
        } catch(InvalidEntityException e) {
            
            assertEquals("Error comes back as 400 Bad Request", 400, e.getStatusCode());
            assertTrue("There is a strategy-specific error", e.getErrors().get("strategy").size() > 0);
            
        }
    }
    
    @Test
    public void noPlanReturns400() {
        try {
            researcherClient.createSchedulePlan(null);    
        } catch(NullPointerException e) {
            assertEquals("Clear null-pointer message", "Plan object is null", e.getMessage());
        }
    }    
}
