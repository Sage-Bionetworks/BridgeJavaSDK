package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

public interface UserClient {
    
    public UserProfile getProfile();

    public void saveProfile(UserProfile profile);

    public void consentToResearch(ConsentSignature signature);
    
    public void resumeDataSharing();

    public void suspendDataSharing();

    public HealthDataRecord getHealthDataRecord(Tracker tracker, String guid);

    public GuidVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record);

    public void deleteHealthDataRecord(Tracker tracker, String guid);

    public List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate);

    public List<GuidVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records);

    public List<Tracker> getAllTrackers();

    public String getTrackerSchema(Tracker tracker);

    public List<Schedule> getSchedules();

    public Survey getSurvey(GuidCreatedOnVersionHolder keys);

    public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers);

    public SurveyResponse getSurveyResponse(String surveyResponseGuid);

    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers);

    public void deleteSurveyResponse(SurveyResponse response);

}
