package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;

public class SchedulePlanTest {

    private GuidVersionHolder keys;

    private TestUser user;
    private TestUser developer;
    private DeveloperClient developerClient;
    private UserClient userClient;
    
    @Before
    public void before() {
        developer = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true, Roles.DEVELOPER);
        user = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);

        developerClient = developer.getSession().getDeveloperClient();
        userClient = user.getSession().getUserClient();
    }

    @After
    public void after() {
        try {
            // Try and clean up if that didn't happen in the test.
            if (keys != null) {
                developerClient.deleteSchedulePlan(keys.getGuid());
            }
            for (SchedulePlan plan : developerClient.getSchedulePlans()) {
                developerClient.deleteSchedulePlan(plan.getGuid());
            }
            assertEquals("Test should have deleted all schedule plans.", developerClient.getSchedulePlans().getTotal(), 0);
        } finally {
            developer.signOutAndDeleteUser();
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void normalUserCannotAccess() {
        TestUser normalUser = null;
        try {
            normalUser = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);
            SchedulePlan plan = Tests.getABTestSchedulePlan();
            normalUser.getSession().getDeveloperClient().createSchedulePlan(plan);
            fail("Should have returned Forbidden status");
        } catch (UnauthorizedException e) {
            assertEquals("Non-researcher gets 403 forbidden", 403, e.getStatusCode());
        } finally {
            normalUser.signOutAndDeleteUser();
        }
    }

    @Test
    public void crudSchedulePlan() throws Exception {
        SchedulePlan plan = Tests.getABTestSchedulePlan();

        // Create
        
        assertNull(plan.getVersion());
        keys = developerClient.createSchedulePlan(plan);
        assertEquals(keys.getGuid(), plan.getGuid());
        assertEquals(keys.getVersion(), plan.getVersion());

        // Update
        plan = developerClient.getSchedulePlan(keys.getGuid());
        SchedulePlan simplePlan = Tests.getSimpleSchedulePlan();
        plan.setStrategy(simplePlan.getStrategy());

        GuidVersionHolder newKeys = developerClient.updateSchedulePlan(plan);
        assertNotEquals("Version should be updated", keys.getVersion(), newKeys.getVersion());
        assertEquals(newKeys.getVersion(), plan.getVersion());

        // Get
        plan = developerClient.getSchedulePlan(keys.getGuid());
        assertEquals("Strategy type has been changed", "SimpleScheduleStrategy", plan.getStrategy().getClass()
                .getSimpleName());

        ResourceList<Schedule> schedules = userClient.getSchedules();
        assertTrue("Schedules exist", !schedules.getItems().isEmpty());

        // Delete
        developerClient.deleteSchedulePlan(keys.getGuid());

        try {
            developerClient.getSchedulePlan(keys.getGuid());
            fail("Should have thrown an exception because plan was deleted");
        } catch (EntityNotFoundException e) {
            assertEquals("Returns 404 Not Found", 404, e.getStatusCode());
            keys = null;
        }
    }

    @Test
    public void invalidPlanReturns400Error() {
        try {
            SchedulePlan plan = Tests.getABTestSchedulePlan();
            plan.setStrategy(null);
            developerClient.createSchedulePlan(plan);
            fail("Plan was invalid and should have thrown an exception");
        } catch (InvalidEntityException e) {
            assertEquals("Error comes back as 400 Bad Request", 400, e.getStatusCode());
            assertTrue("There is a strategy-specific error", e.getErrors().get("strategy").size() > 0);
        }
    }

    @Test
    public void noPlanReturns400() {
        try {
            developerClient.createSchedulePlan(null);
            fail("createSchedulePlan(null) should have thrown an exception");
        } catch (NullPointerException e) {
            assertEquals("Clear null-pointer message", "SchedulePlan cannot be null.", e.getMessage());
        }
    }
    
    @Test
    public void planCanPointToPublishedSurvey() {
        // Can we point to the most recently published survey, rather than a specific version?
        SchedulePlan plan = Tests.getSimpleSchedulePlan();
        SimpleScheduleStrategy strategy = (SimpleScheduleStrategy)plan.getStrategy();
        
        String ref = ClientProvider.getConfig().getEnvironment().getUrl()
                + ClientProvider.getConfig().getRecentlyPublishedSurveyUserApi("AAA");
        
        Activity activity = new Activity("Test", ref);
        assertEquals(ActivityType.SURVEY, activity.getActivityType());

        strategy.getSchedule().getActivities().clear();
        strategy.getSchedule().getActivities().add(activity);
        
        GuidVersionHolder keys = developerClient.createSchedulePlan(plan);
        SchedulePlan newPlan = developerClient.getSchedulePlan(keys.getGuid());
        
        // values that are not updated passed over for simple equality comparison
        plan.setGuid(keys.getGuid());
        plan.setVersion(keys.getVersion());
        plan.setModifiedOn(newPlan.getModifiedOn());
        
        assertEquals(plan, newPlan);
    }
}
