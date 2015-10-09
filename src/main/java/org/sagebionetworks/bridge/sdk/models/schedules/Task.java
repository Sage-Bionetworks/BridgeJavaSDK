package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Task {

    private final String guid;
    private final Activity activity;
    private final DateTime scheduledOn;
    private final DateTime expiresOn;
    private DateTime startedOn;
    private DateTime finishedOn;
    private final Integer minAppVersion;
    private final Integer maxAppVersion;
    private boolean persistent;

    Task(@JsonProperty("guid") String guid, @JsonProperty("activity") Activity activity,
        @JsonProperty("scheduledOn") DateTime scheduledOn, @JsonProperty("expiresOn") DateTime expiresOn,
        @JsonProperty("startedOn") DateTime startedOn, @JsonProperty("finishedOn") DateTime finishedOn, 
        @JsonProperty("minAppVersion") Integer minAppVersion, @JsonProperty("maxAppVersion") Integer maxAppVersion, 
        @JsonProperty("persistent") boolean persistent) {
        this.guid = guid;
        this.activity = activity;
        this.scheduledOn = scheduledOn;
        this.expiresOn = expiresOn;
        this.startedOn = startedOn;
        this.finishedOn = finishedOn;
        this.persistent = persistent;
        this.minAppVersion = minAppVersion;
        this.maxAppVersion = maxAppVersion;
    }
    
    public TaskStatus getStatus() {
        if (finishedOn != null && startedOn == null) {
            return TaskStatus.DELETED;
        } else if (finishedOn != null && startedOn != null) {
            return TaskStatus.FINISHED;
        } else if (startedOn != null) {
            return TaskStatus.STARTED;
        } else if (expiresOn != null && DateTime.now().isAfter(expiresOn)) {
            return TaskStatus.EXPIRED;
        } else if (scheduledOn != null && DateTime.now().isBefore(scheduledOn)) {
            return TaskStatus.SCHEDULED;
        }
        return TaskStatus.AVAILABLE;
    }

    public String getGuid() {
        return guid;
    }
    public Activity getActivity() {
        return activity;
    }
    public DateTime getScheduledOn() {
        return scheduledOn;
    }
    public DateTime getExpiresOn() {
        return expiresOn;
    }
    public DateTime getStartedOn() {
        return startedOn;
    }
    public void setStartedOn(DateTime startedOn) {
        this.startedOn = startedOn;
    }
    public DateTime getFinishedOn() {
        return finishedOn;
    }
    public void setFinishedOn(DateTime finishedOn) {
        this.finishedOn = finishedOn;
    }
    public Integer getMinAppVersion() {
        return minAppVersion;
    }
    public Integer getMaxAppVersion() {
        return maxAppVersion;
    }
    public boolean getPersistent() {
        return persistent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(activity);
        result = prime * result + Objects.hashCode(expiresOn);
        result = prime * result + Objects.hashCode(finishedOn);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(scheduledOn);
        result = prime * result + Objects.hashCode(startedOn);
        result = prime * result + Objects.hashCode(minAppVersion);
        result = prime * result + Objects.hashCode(maxAppVersion);
        result = prime * result + Objects.hashCode(persistent);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        return (Objects.equals(activity, other.activity) && Objects.equals(expiresOn, other.expiresOn) && 
                Objects.equals(finishedOn, other.finishedOn) && Objects.equals(guid, other.guid) && 
                Objects.equals(scheduledOn, other.scheduledOn) && Objects.equals(startedOn, other.startedOn) && 
                Objects.equals(persistent, other.persistent) && Objects.equals(minAppVersion, other.minAppVersion) && 
                Objects.equals(maxAppVersion, other.maxAppVersion));
    }

    @Override
    public String toString() {
        return String.format("Task [guid=%s, activity=%s, scheduledOn=%s, expiresOn=%s, startedOn=%s, finishedOn=%s, persistent=%s]", 
            guid, activity, scheduledOn, expiresOn, startedOn, finishedOn, persistent);
    }
    
}
