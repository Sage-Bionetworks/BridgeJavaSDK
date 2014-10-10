package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.ResearchConsent;
import org.sagebionetworks.bridge.sdk.models.Tracker;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

public class BridgeUserClient {

    private final ClientProvider provider;
    private final UserProfileApiCaller profileApi;
    private final TrackerApiCaller trackerApi;
    private final HealthDataApiCaller healthDataApi;
    private final ConsentApiCaller consentApi;

    private BridgeUserClient(ClientProvider provider) {
        this.provider = provider;
        this.profileApi = UserProfileApiCaller.valueOf(provider);
        this.trackerApi = TrackerApiCaller.valueOf(provider);
        this.healthDataApi = HealthDataApiCaller.valueOf(provider);
        this.consentApi = ConsentApiCaller.valueOf(provider);
    }

    static BridgeUserClient valueOf(ClientProvider provider) {
        return new BridgeUserClient(provider);
    }

    public ClientProvider getProvider() {
        return this.provider;
    }

    /*
     * UserProfile API
     */
    public UserProfile getProfile() {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        }
        return profileApi.getProfile();
    }

    public void saveProfile(UserProfile profile) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null.");
        }
        profileApi.updateProfile(profile);
    }

    /*
     * Health Data API
     */

    public HealthDataRecord getHealthDataRecord(Tracker tracker, long recordId) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (tracker == null) {
            throw new IllegalArgumentException("Tracker cannot be null.");
        }
        return healthDataApi.getHealthDataRecord(tracker, recordId);
    }

    public IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (tracker == null) {
            throw new IllegalArgumentException("Tracker cannot be null.");
        } else if (record == null) {
            throw new IllegalArgumentException("Record cannot be null.");
        }
        return healthDataApi.updateHealthDataRecord(tracker, record);
    }

    public boolean deleteHealthDataRecord(Tracker tracker, long recordId) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (tracker == null) {
            throw new IllegalArgumentException("Tracker cannot be null.");
        }
        return healthDataApi.deleteHealthDataRecord(tracker, recordId);
    }

    public List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (tracker == null) {
            throw new IllegalArgumentException("Tracker cannot be null.");
        } else if (startDate == null) {
            throw new IllegalArgumentException("StartDate cannot be null.");
        } else if (endDate == null) {
            throw new IllegalArgumentException("EndDate cannot be null.");
        } else if (endDate.isBefore(startDate)) {
            throw new IndexOutOfBoundsException("EndDate cannot be before StartDate: " +
                    "startDate=" + startDate.toString(ISODateTimeFormat.dateTime()) +
                    ", endDate=" + endDate.toString(ISODateTimeFormat.dateTime()));
        }
        return healthDataApi.getHealthDataRecordsInRange(tracker, startDate, endDate);
    }

    public List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (tracker == null) {
            throw new IllegalArgumentException("Tracker cannot be null.");
        } else if (records == null) {
            throw new IllegalArgumentException("Records cannot be null.");
        } else if (records.size() == 0) {
            throw new IllegalArgumentException("Need to add at least one record. (records.size() = 0)");
        }
        return healthDataApi.addHealthDataRecords(tracker, records);
    }

    /*
     * Tracker API
     */
    public List<Tracker> getAllTrackers() {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        }
        return trackerApi.getAllTrackers();
    }

    public String getSchema(Tracker tracker) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this  method.");
        }
        return trackerApi.getSchema(tracker);
    }

    /*
     * Consent API
     */
    public void consentToResearch(ResearchConsent consent) {
        if (consent == null) {
            throw new IllegalArgumentException("Consent cannot be null.");
        }

        UserSession session = consentApi.consentToResearch(consent);
        provider.setSession(session);
    }




}
