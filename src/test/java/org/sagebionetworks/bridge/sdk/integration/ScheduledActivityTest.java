package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ClientInfo;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduledActivity;
import org.sagebionetworks.bridge.sdk.models.schedules.TaskReference;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduledActivityStatus;

@Category(IntegrationSmokeTest.class)
public class ScheduledActivityTest {
    
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
        
        Schedule schedule = new Schedule();
        schedule.setLabel("Schedule 1");
        schedule.setDelay("P3D");
        schedule.setScheduleType(ScheduleType.ONCE);
        schedule.addTimes("10:00");
        schedule.addActivity(new Activity("Activity 1", "", new TaskReference("task1")));

        SchedulePlan plan = new SchedulePlan();
        plan.setLabel("Schedule plan 1");
        plan.setSchedule(schedule);
        plan.setMinAppVersion(2);
        plan.setMaxAppVersion(4);
        developerClient.createSchedulePlan(plan);
    }

    @After
    public void after() {
        ClientProvider.setClientInfo(Tests.TEST_CLIENT_INFO);
        try {
            for (SchedulePlan plan : developerClient.getSchedulePlans()) {
                developerClient.deleteSchedulePlan(plan.getGuid());
            }
        } finally {
            developer.signOutAndDeleteUser();
            user.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void createSchedulePlanGetScheduledActivities() {
        // At first, we are an application way outside the bounds of the target, nothing should be returned
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(10).build());
        ResourceList<ScheduledActivity> scheduledActivities = userClient.getScheduledActivities(4, DateTimeZone.getDefault());
        assertEquals("no activities returned, app version too high", 0, scheduledActivities.getTotal());
        
        // Two however... that's fine
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(2).build());
        scheduledActivities = userClient.getScheduledActivities(4, DateTimeZone.getDefault());
        assertEquals("one activity returned", 1, scheduledActivities.getTotal());
        
        // Check again... with a higher app version, the activity won't be returned.
        // This verifies that even after an activity is created, we will still filter it
        // when retrieved from the server (not just when creating activities).
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(10).build());
        scheduledActivities = userClient.getScheduledActivities(4, DateTimeZone.getDefault());
        assertEquals("no activities returned, app version too high", 0, scheduledActivities.getTotal());
        
        // Get that activity again for the rest of the test
        ClientProvider.setClientInfo(new ClientInfo.Builder().withAppName(Tests.APP_NAME).withAppVersion(2).build());
        scheduledActivities = userClient.getScheduledActivities(4, DateTimeZone.getDefault());
        
        ScheduledActivity schActivity = scheduledActivities.get(0);
        assertEquals(ScheduledActivityStatus.SCHEDULED, schActivity.getStatus());
        assertNotNull(schActivity.getScheduledOn());
        assertNull(schActivity.getExpiresOn());
        
        Activity activity = schActivity.getActivity();
        assertEquals(ActivityType.TASK, activity.getActivityType());
        assertEquals("Activity 1", activity.getLabel());
        assertEquals("task1", activity.getTask().getIdentifier());

        schActivity.setStartedOn(DateTime.now());
        userClient.updateScheduledActivities(scheduledActivities.getItems());
        scheduledActivities = userClient.getScheduledActivities(3, DateTimeZone.getDefault());
        assertEquals(1, scheduledActivities.getTotal());
        assertEquals(ScheduledActivityStatus.STARTED, schActivity.getStatus());
        
        schActivity = scheduledActivities.get(0);
        schActivity.setFinishedOn(DateTime.now());
        userClient.updateScheduledActivities(scheduledActivities.getItems());
        scheduledActivities = userClient.getScheduledActivities(3, DateTimeZone.getDefault());
        assertEquals(0, scheduledActivities.getTotal()); // no activities == finished
    }
    
}
