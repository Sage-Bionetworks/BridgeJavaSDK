package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientInfo;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.google.common.collect.Sets;

public class ScheduleTest {

    private static final Set<String> TASK_IDENTIFIERS = Sets.newHashSet("task:AAA", "task:BBB", "task:CCC", "CCC");
    private String planGuid;
    
    private TestUser user;
    private TestUser developer;
    
    @Before
    public void before() {
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(3).build());
        user = TestUserHelper.createAndSignInUser(ScheduleTest.class, true);
        developer = TestUserHelper.createAndSignInUser(ScheduleTest.class, true, Roles.DEVELOPER);
        
        DeveloperClient developerClient = developer.getSession().getDeveloperClient();
        Study study = developerClient.getStudy();
        Set<String> taskIdentifiers = study.getTaskIdentifiers();
        if (!taskIdentifiers.containsAll(TASK_IDENTIFIERS)) {
            taskIdentifiers.addAll(TASK_IDENTIFIERS);
            developerClient.updateStudy(study);
        }
    }
    
    @After
    public void after() {
        ClientProvider.setClientInfo(Tests.TEST_CLIENT_INFO);
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
        DeveloperClient client = developer.getSession().getDeveloperClient();
        planGuid = client.createSchedulePlan(Tests.getSimpleSchedulePlan()).getGuid();
        
        SchedulePlan originalPlan = Tests.getSimpleSchedulePlan();
        SchedulePlan plan = developer.getSession().getDeveloperClient().getSchedulePlan(planGuid);
        // Fields that are set on the server.
        originalPlan.setGuid(plan.getGuid());
        originalPlan.setModifiedOn(plan.getModifiedOn());
        originalPlan.setVersion(plan.getVersion());

        originalPlan.setGuid(plan.getGuid());
        originalPlan.setModifiedOn(plan.getModifiedOn());
        Tests.getActivitiesFromSimpleStrategy(originalPlan).set(0, Tests.getActivityFromSimpleStrategy(plan));
        
        assertEquals(originalPlan, plan);
    }

    @Test
    public void canRetrieveSchedulesForAUser() throws Exception {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        planGuid = client.createSchedulePlan(Tests.getABTestSchedulePlan()).getGuid();

        final UserClient userClient = user.getSession().getUserClient();
        
        List<Schedule> schedules = userClient.getSchedules().getItems();
        assertEquals("There should be one schedule for this user", 1, schedules.size());
    }
    
    @Test
    public void persistentSchedulePlanMarkedPersistent() throws Exception {
        SchedulePlan plan = Tests.getPersistentSchedulePlan();
        DeveloperClient client = developer.getSession().getDeveloperClient();
        
        planGuid = client.createSchedulePlan(plan).getGuid();

        plan = client.getSchedulePlan(planGuid);
        Schedule schedule = Tests.getSimpleSchedule(plan);
        
        assertEquals(true, schedule.getPersistent());
        assertEquals(true, schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
    }
    
    @Test
    public void simpleSchedulePlanNotMarkedPersistent() throws Exception {
        SchedulePlan plan = Tests.getSimpleSchedulePlan();
        DeveloperClient client = developer.getSession().getDeveloperClient();

        planGuid = client.createSchedulePlan(plan).getGuid();

        plan = client.getSchedulePlan(planGuid);
        Schedule schedule = Tests.getSimpleSchedule(plan);
        
        assertEquals(false, schedule.getPersistent());
        assertEquals(false, schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
    }
    
}
