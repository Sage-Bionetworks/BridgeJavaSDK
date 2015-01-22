package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

public class SchedulePlanTest {

    private GuidVersionHolder keys;

    private TestUser user;
    private TestUser researcher;
    private ResearcherClient researcherClient;
    private UserClient userClient;
    
    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true, Tests.RESEARCHER_ROLE);
        user = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);

        researcherClient = researcher.getSession().getResearcherClient();
        userClient = user.getSession().getUserClient();

    }

    @After
    public void after() {
        try {
            // Try and clean up if that didn't happen in the test.
            if (keys != null) {
                researcherClient.deleteSchedulePlan(keys.getGuid());
            }
            for (SchedulePlan plan : researcherClient.getSchedulePlans()) {
                researcherClient.deleteSchedulePlan(plan.getGuid());
            }
            assertEquals("Test should have deleted all schedule plans.", researcherClient.getSchedulePlans().getTotal(), 0);
        } finally {
            researcher.signOutAndDeleteUser();
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void normalUserCannotAccess() {
        TestUser normalUser = null;
        try {
            normalUser = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);
            SchedulePlan plan = Tests.getABTestSchedulePlan();
            normalUser.getSession().getResearcherClient().createSchedulePlan(plan);
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
        keys = researcherClient.createSchedulePlan(plan);

        // Update
        plan = researcherClient.getSchedulePlan(keys.getGuid());
        SchedulePlan simplePlan = Tests.getSimpleSchedulePlan();
        plan.setStrategy(simplePlan.getStrategy());

        GuidVersionHolder newKeys = researcherClient.updateSchedulePlan(plan);
        assertNotEquals("Version should be updated", keys.getVersion(), newKeys.getVersion());

        // Get
        plan = researcherClient.getSchedulePlan(keys.getGuid());
        assertEquals("Strategy type has been changed", "SimpleScheduleStrategy", plan.getStrategy().getClass()
                .getSimpleName());

        ResourceList<Schedule> schedules = userClient.getSchedules();
        assertTrue("Schedules exist", !schedules.getItems().isEmpty());

        // Delete
        researcherClient.deleteSchedulePlan(keys.getGuid());

        try {
            researcherClient.getSchedulePlan(keys.getGuid());
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
            researcherClient.createSchedulePlan(plan);
            fail("Plan was invalid and should have thrown an exception");
        } catch (InvalidEntityException e) {
            assertEquals("Error comes back as 400 Bad Request", 400, e.getStatusCode());
            assertTrue("There is a strategy-specific error", e.getErrors().get("strategy").size() > 0);
        }
    }

    @Test
    public void noPlanReturns400() {
        try {
            researcherClient.createSchedulePlan(null);
            fail("createSchedulePlan(null) should have thrown an exception");
        } catch (NullPointerException e) {
            assertEquals("Clear null-pointer message", "SchedulePlan cannot be null.", e.getMessage());
        }
    }
}
