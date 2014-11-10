package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

public interface UserClient {

    /**
     * Retrieve the UserProfile associated with the currently signed in account.
     *
     * @return UserProfile
     */
    public UserProfile getProfile();

    /**
     * Update the UserProfile associated with the currently signed in account.
     *
     * @param profile
     *            changed profile object that will be used to update UserProfile info on Bridge.
     */
    public void saveProfile(UserProfile profile);

    /**
     * Consent to research.
     *
     * @param signature
     *            Name and Date of consenter's signature.
     */
    public void consentToResearch(ConsentSignature signature);

    /**
     * Resume data sharing on the currently signed in account.
     */
    public void resumeDataSharing();

    /**
     * Suspend data sharing on the currently signed in account.
     */
    public void suspendDataSharing();

    /**
     * Retrieve a health data record associated with the tracker and recordId.
     *
     * @param tracker
     *
     * @param recordId
     *            The guid string that identifies the health data record.
     * @return
     */
    public HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId);

    /**
     * Update a health data record associated with the tracker and recordId stored in the record parameter.
     *
     * @param tracker
     *
     * @param record
     *            The HealthDataRecord that Bridge will use to update its copy of the associated record.
     * @return
     */
    public GuidVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record);

    /**
     * Delete a health data record associated with the tracker and the recordId.
     *
     * @param tracker
     * @param recordId
     *            The guid string that identifies the health data record.
     */
    public void deleteHealthDataRecord(Tracker tracker, String recordId);

    /**
     * Get all HealthDataRecords in the time range set by start date and end date associated with a given tracker.
     *
     * @param tracker
     *
     * @param startDate
     *            The start date, with granularity to milliseconds, to search for HealthDataRecords.
     * @param endDate
     *            The start date, with granularity to milliseconds, to search for HealthDataRecords.
     * @return List<HealthDataRecord>
     */
    public ResourceList<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate);

    /**
     * Add a group of HealthDataRecords to an associated tracker.
     *
     * @param tracker
     *
     * @param records
     *            The list of HealthDataRecords to add to Bridge.
     * @return List<IdVersionHolder> A list of objects that hold the ID and version of each HealthDataRecord added to
     *         Bridge.
     */
    public ResourceList<GuidVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records);

    /**
     * Get all trackers on Bridge associated with a study.
     *
     * @return List<Tracker>
     */
    public ResourceList<Tracker> getAllTrackers();

    /**
     * Get the JSON schema for a tracker. The JSON schema will tell you what data is in a tracker, and how it is
     * organized.
     *
     * @param tracker
     *
     * @return String The JSON Schema as a string.
     */
    public String getTrackerSchema(Tracker tracker);

    /**
     * Get all schedules associated with a study.
     *
     * @return List<Schedule>
     */
    public ResourceList<Schedule> getSchedules();

    /**
     * Get a survey versionedOn a particular DateTime and associated with the given guid String.
     *
     * @param keys
     *
     * @return Survey
     */
    public Survey getSurvey(GuidCreatedOnVersionHolder keys);

    /**
     * Submit a list of SurveyAnswers to a particular survey.
     *
     * @param survey
     *            The survey that the answers will be added to.
     * @param answers
     *            The answers to add to the survey.
     * @return GuidHolder A holder storing the GUID of the survey.
     */
    public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers);

    /**
     * Get the survey response associated with the guid string paramater.
     *
     * @param surveyResponseGuid
     *            The GUID identifying the SurveyResponse
     * @return SurveyResponse
     */
    public SurveyResponse getSurveyResponse(String surveyResponseGuid);

    /**
     * Add a list of SurveyAnswers to a SurveyResponse.
     *
     * @param response
     *            The response that answers will be added to.
     * @param answers
     *            The answers that will be added to the response.
     */
    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers);

    /**
     * Delete a particular survey response.
     *
     * @param response
     *            The response to delete.
     */
    public void deleteSurveyResponse(SurveyResponse response);

    /**
     * Request an upload session from the user.
     *
     * @param request
     *            the request object Bridge uses to create the Upload Session.
     * @return UploadSession
     */
    public UploadSession requestUploadSession(UploadRequest request);

    /**
     * Upload a file using the requested UploadSession. Closes the upload after it's done.
     *
     * @param session
     *            The session used to upload.
     * @param fileName
     *            File to upload.
     */
    public void upload(UploadSession session, UploadRequest request, String fileName);

}
