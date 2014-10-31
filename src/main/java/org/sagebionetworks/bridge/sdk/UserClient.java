package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.Tracker;
import org.sagebionetworks.bridge.sdk.models.UserProfile;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

public interface UserClient {
    
    public UserProfile getProfile();

    public void saveProfile(UserProfile profile);

    public void consentToResearch(ConsentSignature signature);
    
    public void resumeDataSharing();

    public void suspendDataSharing();

    public HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId);

    public IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record);

    public void deleteHealthDataRecord(Tracker tracker, String recordId);

    public List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate);

    public List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records);

    public List<Tracker> getAllTrackers();

    public String getTrackerSchema(Tracker tracker);

    public List<Schedule> getSchedules();

    public Survey getSurvey(String surveyGuid, DateTime versionedOn);

    public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers);

    public SurveyResponse getSurveyResponse(String surveyResponseGuid);

    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers);

    public void deleteSurveyResponse(SurveyResponse response);

}
