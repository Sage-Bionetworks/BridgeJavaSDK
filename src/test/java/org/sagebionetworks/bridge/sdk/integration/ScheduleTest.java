package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;

public class ScheduleTest {

    private String planGuid;
    
    private TestUser user;
    private TestUser developer;
    
    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(ScheduleTest.class, true);
        developer = TestUserHelper.createAndSignInUser(ScheduleTest.class, true, Roles.DEVELOPER);
        
        DeveloperClient client = developer.getSession().getDeveloperClient();
        planGuid = client.createSchedulePlan(Tests.getABTestSchedulePlan()).getGuid();
    }
    
    @After
    public void after() {
        try {
            DeveloperClient client = developer.getSession().getDeveloperClient();
            client.deleteSchedulePlan(planGuid);
        } finally {
            user.signOutAndDeleteUser();
            developer.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void schedulePlanIsCorrect() throws Exception {
        SchedulePlan originalPlan = Tests.getABTestSchedulePlan();
        SchedulePlan plan = developer.getSession().getDeveloperClient().getSchedulePlan(planGuid);
        // Fields that are set on the server.
        originalPlan.setGuid(plan.getGuid());
        originalPlan.setModifiedOn(plan.getModifiedOn());
        originalPlan.setVersion(plan.getVersion());
        assertEquals(originalPlan, plan);
    }
    
    @Test
    public void canRetrieveSchedulesForAUser() throws Exception {
        final UserClient client = user.getSession().getUserClient();
        
        List<Schedule> schedules = client.getSchedules().getItems();
        assertEquals("There should be one schedule for this user", 1, schedules.size());
    }
    
}
