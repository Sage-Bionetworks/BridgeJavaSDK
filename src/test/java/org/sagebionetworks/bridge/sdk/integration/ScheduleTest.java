package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.sagebionetworks.bridge.Tests.RESEARCHER_ROLE;
import static org.sagebionetworks.bridge.Tests.untilConsistent;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.integration.SchedulePlanTest.TestABSchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;

public class ScheduleTest {

    private String planGuid;
    
    private TestUser user;
    private TestUser researcher;
    
    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(ScheduleTest.class, true);
        researcher = TestUserHelper.createAndSignInUser(ScheduleTest.class, true, RESEARCHER_ROLE);
        
        ResearcherClient client = researcher.getSession().getResearcherClient();
        planGuid = client.createSchedulePlan(new TestABSchedulePlan()).getGuid();
    }
    
    @After
    public void after() {
        try {
            ResearcherClient client = researcher.getSession().getResearcherClient();
            client.deleteSchedulePlan(planGuid);
        } finally {
            user.signOutAndDeleteUser();
            researcher.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void canRetrieveSchedulesForAUser() throws Exception {
        final UserClient client = user.getSession().getUserClient();
        
        untilConsistent(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return !client.getSchedules().getItems().isEmpty();
            }
        });
        
        List<Schedule> schedules = client.getSchedules().getItems();
        assertEquals("There should be one schedule for this user", 1, schedules.size());
    }
    
}
