package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.Tracker;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.UserProfile;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

class BridgeUserClient implements UserClient {

    private final Session session;
    private final UserProfileApiCaller profileApi;
    private final ConsentApiCaller consentApi;
    private final TrackerApiCaller trackerApi;
    private final HealthDataApiCaller healthDataApi;
    private final ScheduleApiCaller scheduleApi;
    private final SurveyResponseApiCaller surveyResponseApi;
    private final UploadApiCaller uploadApi;

    private BridgeUserClient(Session session) {
        this.session = session;
        this.profileApi = UserProfileApiCaller.valueOf(session);
        this.consentApi = ConsentApiCaller.valueOf(session);
        this.trackerApi = TrackerApiCaller.valueOf(session);
        this.healthDataApi = HealthDataApiCaller.valueOf(session);
        this.scheduleApi = ScheduleApiCaller.valueOf(session);
        this.surveyResponseApi = SurveyResponseApiCaller.valueOf(session);
        this.uploadApi = UploadApiCaller.valueOf(session);
    }

    static BridgeUserClient valueOf(Session session) {
        return new BridgeUserClient(session);
    }

    /*
     * UserProfile API
     */
    @Override
    public UserProfile getProfile() {
        session.checkSignedIn();

        return profileApi.getProfile();
    }

    @Override
    public void saveProfile(UserProfile profile) {
        session.checkSignedIn();
        checkNotNull(profile, "Profile cannot be null.");

        profileApi.updateProfile(profile);
    }

    /*
     * Consent API
     */
    @Override
    public void resumeDataSharing() {
        session.checkSignedIn();

        consentApi.resumeDataSharing();
    }

    @Override
    public void suspendDataSharing() {
        session.checkSignedIn();

        consentApi.suspendDataSharing();
    }

    /*
     * Health Data API
     */
    @Override
    public HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkArgument(isNotBlank(recordId), "recordId cannot be null or empty.");

        return healthDataApi.getHealthDataRecord(tracker, recordId);
    }

    @Override
    public IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(record, "Record cannot be null.");

        return healthDataApi.updateHealthDataRecord(tracker, record);
    }

    @Override
    public void deleteHealthDataRecord(Tracker tracker, String recordId) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkArgument(isNotBlank(recordId),"recordId cannot be null or empty.");

        healthDataApi.deleteHealthDataRecord(tracker, recordId);
    }

    @Override
    public List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(startDate, "startDate cannot be null.");
        checkNotNull(endDate, "endDate cannot be null.");
        checkArgument(endDate.isAfter(startDate), "endDate must be after startDate.");

        return healthDataApi.getHealthDataRecordsInRange(tracker, startDate, endDate);
    }

    @Override
    public List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(records, "Records cannot be null.");

        return healthDataApi.addHealthDataRecords(tracker, records);
    }

    /*
     * Tracker API
     */
    @Override
    public List<Tracker> getAllTrackers() {
        session.checkSignedIn();

        return trackerApi.getAllTrackers();
    }

    @Override
    public String getTrackerSchema(Tracker tracker) {
        session.checkSignedIn();

        return trackerApi.getTrackerSchema(tracker);
    }

    /*
     * Schedules API
     */
    @Override
    public List<Schedule> getSchedules() {
        session.checkSignedIn();
        return scheduleApi.getSchedules();
    }

    /*
     * Survey Response API
     */
    @Override
    public Survey getSurvey(String surveyGuid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(surveyGuid), "SurveyGuid cannot be null or empty.");
        checkNotNull(versionedOn, "VersionedOn cannot be null.");

        return surveyResponseApi.getSurvey(surveyGuid, versionedOn);
    }

    @Override
    public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey cannot be null.");
        checkArgument(isNotBlank(survey.getGuid()), "Survey guid cannot be null or empty.");
        checkNotNull(survey.getVersionedOn(), "Survey versionedOn cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        return surveyResponseApi.submitAnswers(answers, survey.getGuid(), survey.getVersionedOn());
    }

    @Override
    public SurveyResponse getSurveyResponse(String surveyResponseGuid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(surveyResponseGuid), "SurveyResponseGuid cannot be null or empty.");

        return surveyResponseApi.getSurveyResponse(surveyResponseGuid);
    }

    @Override
    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(response, "Response cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        surveyResponseApi.addAnswersToSurveyResponse(answers, response.getGuid());
    }

    @Override
    public void deleteSurveyResponse(SurveyResponse response) {
        session.checkSignedIn();
        checkNotNull(response, "Response cannot be null.");

        surveyResponseApi.deleteSurveyResponse(response.getGuid());
    }

    @Override
    public UploadSession requestUploadSession(UploadRequest request) {
        session.checkSignedIn();
        checkNotNull(request, "Request cannot be null.");

        return uploadApi.requestUploadSession(request);
    }

    @Override
    public void upload(UploadSession session, UploadRequest request, String fileName) {
        this.session.checkSignedIn();
        checkNotNull(session, "session cannot be null.");
        checkNotNull(fileName, "fileName cannot be null.");
        checkArgument(session.getExpires().isAfter(DateTime.now()), "session expiration must be greater than 0.");

        uploadApi.upload(session, request, fileName);
        uploadApi.close(session);
    }
}
