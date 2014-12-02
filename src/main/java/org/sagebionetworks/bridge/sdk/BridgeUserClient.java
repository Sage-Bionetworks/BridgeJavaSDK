package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.ActivityType;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeUserClient extends BaseApiCaller implements UserClient {

    private final TypeReference<ResourceListImpl<Tracker>> tType = new TypeReference<ResourceListImpl<Tracker>>() {};
    private final TypeReference<ResourceListImpl<Schedule>> sType = new TypeReference<ResourceListImpl<Schedule>>() {};
    private static final TypeReference<ResourceListImpl<HealthDataRecord>> hdrType = 
            new TypeReference<ResourceListImpl<HealthDataRecord>>() {};
    private static final TypeReference<ResourceListImpl<GuidVersionHolder>> gvhType = 
            new TypeReference<ResourceListImpl<GuidVersionHolder>>() {};

    private BridgeUserClient(BridgeSession session) {
        super(session);
    }

    static BridgeUserClient valueOf(BridgeSession session) {
        return new BridgeUserClient(session);
    }

    /*
     * UserProfile API
     */
    @Override
    public UserProfile getProfile() {
        session.checkSignedIn();
        
        return get(config.getProfileApi(), UserProfile.class);
    }

    @Override
    public void saveProfile(UserProfile profile) {
        session.checkSignedIn();
        checkNotNull(profile, "Profile cannot be null.");

        post(config.getProfileApi(), profile);
        session.setUsername(profile.getUsername());
    }

    /*
     * Consent API
     */

    @Override
    public void consentToResearch(ConsentSignature signature) {
        session.checkSignedIn();

        checkNotNull(signature, Bridge.CANNOT_BE_NULL, "ConsentSignature");
        post(config.getConsentApi(), signature);
        session.setConsented(true);
    }

    @Override
    public ConsentSignature getConsentSignature() {
        session.checkSignedIn();
        ConsentSignature sig = get(config.getConsentApi(), ConsentSignature.class);
        return sig;
    }

    @Override
    public void resumeDataSharing() {
        session.checkSignedIn();

        post(config.getConsentResumeApi());
        session.setDataSharing(true);
    }

    @Override
    public void suspendDataSharing() {
        session.checkSignedIn();

        post(config.getConsentSuspendApi());
        session.setDataSharing(false);
    }

    /*
     * Health Data API
     */
    @Override
    public HealthDataRecord getHealthDataRecord(Tracker tracker, String guid) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkArgument(isNotBlank(guid), "guid cannot be null or empty.");

        String trackerId = tracker.getIdentifier();
        return get(config.getHealthDataRecordApi(trackerId, guid), HealthDataRecord.class);
    }

    @Override
    public GuidVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(record, "Record cannot be null.");

        String trackerId = tracker.getIdentifier();
        return post(config.getHealthDataRecordApi(trackerId, record.getGuid()), record, SimpleGuidVersionHolder.class);
    }

    @Override
    public void deleteHealthDataRecord(Tracker tracker, String guid) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkArgument(isNotBlank(guid),"guid cannot be null or empty.");

        String trackerId = tracker.getIdentifier();
        delete(config.getHealthDataRecordApi(trackerId, guid));
    }

    @Override
    public ResourceList<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(startDate, "startDate cannot be null.");
        checkNotNull(endDate, "endDate cannot be null.");
        checkArgument(endDate.isAfter(startDate), "endDate must be after startDate.");

        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String trackerId = tracker.getIdentifier();
        return get(config.getHealthDataTrackerApi(trackerId) + toQueryString(queryParameters), hdrType);
    }

    @Override
    public ResourceList<GuidVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(records, "Records cannot be null.");

        String trackerId = tracker.getIdentifier();
        return post(config.getHealthDataTrackerApi(trackerId), records, gvhType);
    }

    /*
     * Tracker API
     */
    @Override
    public ResourceList<Tracker> getAllTrackers() {
        session.checkSignedIn();

        return get(config.getTrackerApi(), tType);
    }

    @Override
    public String getTrackerSchema(Tracker tracker) {
        session.checkSignedIn();

        HttpResponse response = get(tracker.getSchemaUrl());
        return getResponseBody(response);
    }

    /*
     * Schedules API
     */
    @Override
    public ResourceList<Schedule> getSchedules() {
        session.checkSignedIn();
        return get(config.getSchedulesApi(), sType);
    }

    /*
     * Survey Response API
     */
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");

        return get(config.getSurveyUserApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    @Override
    public Survey getSurvey(Schedule schedule) {
        session.checkSignedIn();
        checkNotNull(schedule, Bridge.CANNOT_BE_NULL, "schedule");
        checkArgument(schedule.getActivityType() == ActivityType.survey, "schedule is not for a survey");
        
        String[] parts = schedule.getActivityRef().split("/surveys/")[1].split("/");
        String guid = parts[0];
        DateTime createdOn = DateTime.parse(parts[1]);
        return getSurvey(new SimpleGuidCreatedOnVersionHolder(guid, createdOn, null));
    }

    @Override
    public GuidHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey cannot be null.");
        checkArgument(isNotBlank(survey.getGuid()), "Survey guid cannot be null or empty.");
        checkNotNull(survey.getCreatedOn(), "Survey createdOn cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        return post(config.getSurveyUserApi(survey.getGuid(), survey.getCreatedOn()), answers, SimpleGuidHolder.class);
    }

    @Override
    public SurveyResponse getSurveyResponse(String surveyResponseGuid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(surveyResponseGuid), "SurveyResponseGuid cannot be null or empty.");

        return get(config.getSurveyResponseApi(surveyResponseGuid), SurveyResponse.class);
    }

    @Override
    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(response, "Response cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        post(config.getSurveyResponseApi(response.getGuid()), answers);
    }

    @Override
    public void deleteSurveyResponse(SurveyResponse response) {
        session.checkSignedIn();
        checkNotNull(response, "Response cannot be null.");

        delete(config.getSurveyResponseApi(response.getGuid()));
    }

    /*
     * Upload API
     */

    @Override
    public UploadSession requestUploadSession(UploadRequest request) {
        session.checkSignedIn();
        checkNotNull(request, "Request cannot be null.");

        return post(config.getUploadApi(), request, UploadSession.class);
    }

    @Override
    public void upload(UploadSession session, UploadRequest request, String fileName) {
        this.session.checkSignedIn();
        checkNotNull(session, "session cannot be null.");
        checkNotNull(fileName, "fileName cannot be null.");
        checkArgument(session.getExpires().isAfter(DateTime.now()), "session already expired, cannot upload.");

        HttpEntity entity = null;
        try {
            byte[] b = Files.readAllBytes(Paths.get(fileName));
            entity = new ByteArrayEntity(b, ContentType.create(request.getContentType()));
        } catch (FileNotFoundException e) {
            throw new BridgeSDKException(e);
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }
        String url = session.getUrl().toString();
        s3Put(url, entity, request);
        
        // NOTE: Is this really how it's supposed to work? Close right away?
        post(config.getUploadCompleteApi(session.getId()));
    }
}
