package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.schedules.Activity;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.schedules.Task;
import org.sagebionetworks.bridge.sdk.models.schedules.TaskStatus;

@Category(IntegrationSmokeTest.class)
public class TaskTest {
    
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
        
        Schedule schedule = new Schedule();
        schedule.setLabel("Schedule 1");
        schedule.setDelay("P3D");
        schedule.setScheduleType(ScheduleType.ONCE);
        schedule.addTimes("10:00");
        schedule.addActivity(new Activity("Activity 1", "task:task1"));
        
        SchedulePlan plan = new SchedulePlan();
        plan.setLabel("Schedule plan 1");
        plan.setSchedule(schedule);
        researcherClient.createSchedulePlan(plan);
    }

    @After
    public void after() {
        try {
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
    public void createSchedulePlanGetTask() {
        ResourceList<Task> tasks = userClient.getTasks(DateTime.now().plusDays(4));
        assertEquals("one task returned", 1, tasks.getTotal());

        Task task = tasks.get(0);
        assertEquals(TaskStatus.SCHEDULED, task.getStatus());
        assertNotNull(task.getScheduledOn());
        assertNull(task.getExpiresOn());
        
        Activity activity = task.getActivity();
        assertEquals(ActivityType.TASK, activity.getActivityType());
        assertEquals("Activity 1", activity.getLabel());
        assertEquals("task:task1", activity.getRef());

        task.setStartedOn(DateTime.now());
        userClient.updateTasks(tasks.getItems());
        tasks = userClient.getTasks(DateTime.now().plusDays(3));
        assertEquals(1, tasks.getTotal());
        assertEquals(TaskStatus.STARTED, task.getStatus());
        
        task = tasks.get(0);
        task.setFinishedOn(DateTime.now());
        userClient.updateTasks(tasks.getItems());
        tasks = userClient.getTasks(DateTime.now().plusDays(3));
        assertEquals(0, tasks.getTotal()); // no tasks == finished
    }
    
    
}
