package org.sagebionetworks.bridge.sdk.models.schedules;

public enum ScheduledActivityStatus {
    /**
     * Activity that has a scheduled start time in the future and has not been started. It can be 
     * shown to the user, but whether it can be started prior to the scheduled start time 
     * is up to the client application.
     */
    SCHEDULED,
    /**
     * Activity is within the scheduling window but has not been marked as started by the client. 
     */
    AVAILABLE,
    /**
     * The user has started this activity (regardless of scheduling information). 
     */
    STARTED,
    /**
     * The user has finished this activity (regardless of scheduling information).
     */
    FINISHED,
    /**
     * The activity schedule window has passed without the activity being started; at this point 
     * the client probably will not let the user start the activity. 
     */
    EXPIRED,
    /**
     * The activity has a finished timestamp but no startedOn timestamp, indicating the task was 
     * deleted without being started (we currently have no way to indicate the difference between 
     * a started task that was prematurely finished and a started task that was deleted).
     */
    DELETED;
}
