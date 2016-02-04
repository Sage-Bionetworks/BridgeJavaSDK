package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientInfo;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.CriteriaScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleCriteria;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.schedules.SimpleScheduleStrategy;
import org.sagebionetworks.bridge.sdk.models.schedules.SurveyReference;
import org.sagebionetworks.bridge.sdk.models.schedules.TaskReference;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SchedulePlanTest {

    private static final ObjectMapper MAPPER = Utilities.getMapper();

    private TestUser admin;
    private TestUser user;
    private TestUser developer;
    private DeveloperClient developerClient;
    private UserClient userClient;

    @Before
    public void before() {
        admin = TestUserHelper.getSignedInAdmin();
        developer = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true, Roles.DEVELOPER);
        user = TestUserHelper.createAndSignInUser(SchedulePlanTest.class, true);

        developerClient = developer.getSession().getDeveloperClient();
        userClient = user.getSession().getUserClient();
    }

    @After
    public void after() {
        ClientProvider.setClientInfo(new ClientInfo.Builder().build());
        developer.signOutAndDeleteUser();
        user.signOutAndDeleteUser();
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
        // We want a version that will return the schedule plan. Zero doesn't do it.
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(3).build());

        SchedulePlan plan = Tests.getABTestSchedulePlan();

        // Create
        assertNull(plan.getVersion());
        GuidVersionHolder keys = developerClient.createSchedulePlan(plan);
        assertEquals(keys.getGuid(), plan.getGuid());
        assertEquals(keys.getVersion(), plan.getVersion());

        plan = developerClient.getSchedulePlan(keys.getGuid());
        // Verify some fields are correct
        assertNotNull(plan.getGuid());
        assertNotNull(plan.getModifiedOn());
        assertNotNull(plan.getVersion());
        assertEquals("A/B Test Schedule Plan", plan.getLabel());
        assertEquals(new Integer(2), plan.getMinAppVersion());
        assertEquals(new Integer(8), plan.getMaxAppVersion());

        // Update
        SchedulePlan simplePlan = Tests.getSimpleSchedulePlan();
        plan.setStrategy(simplePlan.getStrategy());

        GuidVersionHolder newKeys = developerClient.updateSchedulePlan(plan);
        assertNotEquals("Version should be updated", keys.getVersion(), newKeys.getVersion());
        assertEquals(newKeys.getVersion(), plan.getVersion());

        // Get
        plan = developerClient.getSchedulePlan(keys.getGuid());
        assertEquals("Strategy type has been changed", "SimpleScheduleStrategy",
                plan.getStrategy().getClass().getSimpleName());

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
    public void criteriaScheduleStrategyPlanCRUD() throws Exception {
        SchedulePlan retrievedPlan = null;
        try {
            // Create plan with a criteria strategy
            Schedule schedule1 = new Schedule();
            schedule1.setLabel("Task 1");
            schedule1.setScheduleType(ScheduleType.ONCE);
            schedule1.addActivity(new Activity("Do task",null,new TaskReference("task:AAA")));
            
            Schedule schedule2 = new Schedule();
            schedule2.setLabel("Task 2");
            schedule2.setScheduleType(ScheduleType.ONCE);
            schedule2.addActivity(new Activity("Do task",null,new TaskReference("task:BBB")));
            
            ScheduleCriteria criteria1 = new ScheduleCriteria.Builder()
                    .withMinAppVersion(2)
                    .withMaxAppVersion(5)
                    .withSchedule(schedule1).build();
            ScheduleCriteria criteria2 = new ScheduleCriteria.Builder()
                    .withMinAppVersion(6)
                    .withMaxAppVersion(10)
                    .withSchedule(schedule2).build();
            
            CriteriaScheduleStrategy strategy = new CriteriaScheduleStrategy();
            strategy.addCriteria(criteria1);
            strategy.addCriteria(criteria2);
            
            SchedulePlan plan = new SchedulePlan();
            plan.setLabel("Criteria schedule plan");
            plan.setStrategy(strategy);
            
            GuidVersionHolder keys = developerClient.createSchedulePlan(plan);
            retrievedPlan = developerClient.getSchedulePlan(keys.getGuid());
            
            assertTrue(retrievedPlan.getStrategy() instanceof CriteriaScheduleStrategy);
            
            CriteriaScheduleStrategy retrievedStrategy = (CriteriaScheduleStrategy)retrievedPlan.getStrategy();
            assertEquals(2, retrievedStrategy.getScheduleCriteria().size());
            criteria1 = updateGuid(criteria1, retrievedStrategy, 0);
            criteria2 = updateGuid(criteria2, retrievedStrategy, 1);
            assertEquals(criteria1, retrievedStrategy.getScheduleCriteria().get(0));
            assertEquals(criteria2, retrievedStrategy.getScheduleCriteria().get(1));
        } finally {
            if (retrievedPlan != null) {
                developerClient.deleteSchedulePlan(retrievedPlan.getGuid());    
            }
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
        GuidCreatedOnVersionHolder surveyKeys = null;
        GuidVersionHolder keys = null;
        try {
            Survey survey = TestSurvey.getSurvey(SchedulePlanTest.class);
            surveyKeys = developerClient.createSurvey(survey);

            // Can we point to the most recently published survey, rather than a specific version?
            SchedulePlan plan = Tests.getSimpleSchedulePlan();
            SimpleScheduleStrategy strategy = (SimpleScheduleStrategy) plan.getStrategy();

            Activity activity = new Activity("Test", null,
                    new SurveyReference(surveyKeys.getGuid(), surveyKeys.getCreatedOn()));
            assertEquals(ActivityType.SURVEY, activity.getActivityType());

            strategy.getSchedule().getActivities().clear();
            strategy.getSchedule().getActivities().add(activity);

            keys = developerClient.createSchedulePlan(plan);
            SchedulePlan newPlan = developerClient.getSchedulePlan(keys.getGuid());

            // values that are not updated passed over for simple equality comparison
            plan.setGuid(newPlan.getGuid());
            plan.setModifiedOn(newPlan.getModifiedOn());
            Tests.getActivitiesFromSimpleStrategy(plan).set(0, Tests.getActivityFromSimpleStrategy(newPlan));

            assertEquals(plan, newPlan);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            developerClient.deleteSchedulePlan(keys.getGuid());
            admin.getSession().getAdminClient().deleteSurveyPermanently(surveyKeys);
        }
    }

    /**
     * ScheduleCriteria should be equal, *except* that the server has added a GUID to the activities.
     * Recreate the original with the GUID (working around the builder).
     * @param original
     * @param updated
     * @param index
     * @return
     * @throws Exception
     */
    private ScheduleCriteria updateGuid(ScheduleCriteria original, CriteriaScheduleStrategy updated, int index)
            throws Exception {
        
        String guid = updated.getScheduleCriteria().get(index).getSchedule().getActivities().get(0).getGuid();
        JsonNode node = MAPPER.valueToTree(original);
        ObjectNode actNode = (ObjectNode)node.get("schedule").get("activities").get(0);
        actNode.put("guid", guid);
        return MAPPER.readValue(node.toString(), ScheduleCriteria.class);
    }
}
