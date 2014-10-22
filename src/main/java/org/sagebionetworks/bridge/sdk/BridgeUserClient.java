package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.Tracker;
import org.sagebionetworks.bridge.sdk.models.UserProfile;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

public class BridgeUserClient {

    private final ClientProvider provider;
    private final UserProfileApiCaller profileApi;
    private final TrackerApiCaller trackerApi;
    private final HealthDataApiCaller healthDataApi;
    private final ScheduleApiCaller scheduleApi;
    private final SurveyResponseApiCaller surveyResponseApi;

    private BridgeUserClient(ClientProvider provider) {
        this.provider = provider;
        this.profileApi = UserProfileApiCaller.valueOf(provider);
        this.trackerApi = TrackerApiCaller.valueOf(provider);
        this.healthDataApi = HealthDataApiCaller.valueOf(provider);
        this.scheduleApi = ScheduleApiCaller.valueOf(provider);
        this.surveyResponseApi = SurveyResponseApiCaller.valueOf(provider);
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
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");

        return profileApi.getProfile();
    }

    public void saveProfile(UserProfile profile) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(profile, "Profile cannot be null.");

        profileApi.updateProfile(profile);
    }

    /*
     * Health Data API
     */

    public HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(tracker, "Tracker cannot be null.");
        Preconditions.checkNotEmpty(recordId, "recordId cannot be null or empty.");

        return healthDataApi.getHealthDataRecord(tracker, recordId);
    }

    public IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(tracker, "Tracker cannot be null.");
        Preconditions.checkNotNull(record, "Record cannot be null.");

        return healthDataApi.updateHealthDataRecord(tracker, record);
    }

    public void deleteHealthDataRecord(Tracker tracker, String recordId) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(tracker, "Tracker cannot be null.");
        Preconditions.checkNotEmpty(recordId, "recordId cannot be null or empty.");

        healthDataApi.deleteHealthDataRecord(tracker, recordId);
    }

    public List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(tracker, "Tracker cannot be null.");
        Preconditions.checkNotNull(startDate, "startDate cannot be null.");
        Preconditions.checkNotNull(endDate, "endDate cannot be null.");
        Preconditions.checkArgument(endDate.isAfter(startDate), "endDate must be after startDate.");

        return healthDataApi.getHealthDataRecordsInRange(tracker, startDate, endDate);
    }

    public List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        Preconditions.checkNotNull(tracker, "Tracker cannot be null.");
        Preconditions.checkNotNull(records, "Records cannot be null.");

        return healthDataApi.addHealthDataRecords(tracker, records);
    }

    /*
     * Tracker API
     */
    public List<Tracker> getAllTrackers() {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");

        return trackerApi.getAllTrackers();
    }

    public String getSchema(Tracker tracker) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");

        return trackerApi.getSchema(tracker);
    }

    // TODO figure out what to do about consent. Can't be signed in when making consent calls.

    /*
     * Schedules API
     */
    public List<Schedule> getSchedules() {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
        return scheduleApi.getSchedules();
    }

    /*
     * Survey Response API
     */

   public Survey getSurvey(String surveyGuid, DateTime versionedOn) {
       Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
       Preconditions.checkNotEmpty(surveyGuid, "SurveyGuid cannot be null or empty.");
       Preconditions.checkNotNull(versionedOn, "VersionedOn cannot be null.");

       return surveyResponseApi.getSurvey(surveyGuid, versionedOn);
   }

   public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers) {
       Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
       Preconditions.checkNotNull(survey, "Survey cannot be null.");
       Preconditions.checkNotNull(answers, "Answers cannot be null.");

       return surveyResponseApi.submitAnswers(answers, survey.getGuid(), survey.getVersionedOn());
   }

   public SurveyResponse getSurveyResponse(String surveyResponseGuid) {
       Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method");
       Preconditions.checkNotEmpty(surveyResponseGuid, "SurveyResponseGuid cannot be null or empty.");

       return surveyResponseApi.getSurveyResponse(surveyResponseGuid);
   }

   public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers) {
       Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method");
       Preconditions.checkNotNull(response, "Response cannot be null.");
       Preconditions.checkNotNull(answers, "Answers cannot be null.");

       surveyResponseApi.addAnswersToSurveyResponse(answers, response.getGuid());
   }

   public void deleteSurveyResponse(SurveyResponse response) {
       Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in to call this method.");
       Preconditions.checkNotNull(response, "Response cannot be null.");

       surveyResponseApi.deleteSurveyResponse(response.getGuid());
   }

   /*
    *
    */


}
