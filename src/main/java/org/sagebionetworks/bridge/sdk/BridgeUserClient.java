package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

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
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleIdentifierHolder;
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

    private static final TypeReference<ResourceListImpl<Tracker>> tType =
            new TypeReference<ResourceListImpl<Tracker>>() {};
    private static final TypeReference<ResourceListImpl<Schedule>> sType =
            new TypeReference<ResourceListImpl<Schedule>>() {};
    private static final TypeReference<ResourceListImpl<HealthDataRecord>> hdrType = 
            new TypeReference<ResourceListImpl<HealthDataRecord>>() {};
    private static final TypeReference<ResourceListImpl<GuidVersionHolder>> gvhType = 
            new TypeReference<ResourceListImpl<GuidVersionHolder>>() {};

    BridgeUserClient(@Nonnull BridgeSession session, @Nonnull ClientProvider clientProvider) {
        super(session, clientProvider);
    }

    /*
     * UserProfile API
     */
    @Override
    public UserProfile getProfile() {
        session.checkSignedIn();
        
        return get(clientProvider.getConfig().getProfileApi(), UserProfile.class);
    }

    @Override
    public void saveProfile(UserProfile profile) {
        session.checkSignedIn();
        checkNotNull(profile, "Profile cannot be null.");

        post(clientProvider.getConfig().getProfileApi(), profile);
        session.setUsername(profile.getUsername());
    }

    /*
     * Consent API
     */

    @Override
    public void consentToResearch(ConsentSignature signature) {
        session.checkSignedIn();

        checkNotNull(signature, Bridge.CANNOT_BE_NULL, "ConsentSignature");
        post(clientProvider.getConfig().getConsentApi(), signature);
        session.setConsented(true);
    }

    @Override
    public ConsentSignature getConsentSignature() {
        session.checkSignedIn();
        ConsentSignature sig = get(clientProvider.getConfig().getConsentApi(), ConsentSignature.class);
        return sig;
    }

    @Override
    public void resumeDataSharing() {
        session.checkSignedIn();

        post(clientProvider.getConfig().getConsentResumeApi());
        session.setDataSharing(true);
    }

    @Override
    public void suspendDataSharing() {
        session.checkSignedIn();

        post(clientProvider.getConfig().getConsentSuspendApi());
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
        return get(clientProvider.getConfig().getHealthDataRecordApi(trackerId, guid), HealthDataRecord.class);
    }

    @Override
    public GuidVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(record, "Record cannot be null.");

        String trackerId = tracker.getIdentifier();
        return post(clientProvider.getConfig().getHealthDataRecordApi(trackerId, record.getGuid()), record, SimpleGuidVersionHolder.class);
    }

    @Override
    public void deleteHealthDataRecord(Tracker tracker, String guid) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkArgument(isNotBlank(guid),"guid cannot be null or empty.");

        String trackerId = tracker.getIdentifier();
        delete(clientProvider.getConfig().getHealthDataRecordApi(trackerId, guid));
    }

    @Override
    public ResourceList<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(startDate, "startDate cannot be null.");
        checkNotNull(endDate, "endDate cannot be null.");
        checkArgument(endDate.isAfter(startDate), "endDate must be after startDate.");

        Map<String,String> queryParameters = new HashMap<>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String trackerId = tracker.getIdentifier();
        return get(clientProvider.getConfig().getHealthDataTrackerApi(trackerId) + toQueryString(queryParameters), hdrType);
    }

    @Override
    public ResourceList<GuidVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        session.checkSignedIn();
        checkNotNull(tracker, "Tracker cannot be null.");
        checkNotNull(records, "Records cannot be null.");

        String trackerId = tracker.getIdentifier();
        return post(clientProvider.getConfig().getHealthDataTrackerApi(trackerId), records, gvhType);
    }

    /*
     * Tracker API
     */
    @Override
    public ResourceList<Tracker> getAllTrackers() {
        session.checkSignedIn();

        return get(clientProvider.getConfig().getTrackerApi(), tType);
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
        return get(clientProvider.getConfig().getSchedulesApi(), sType);
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

        return get(clientProvider.getConfig().getSurveyUserApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    @Override
    public IdentifierHolder submitAnswersToSurvey(Survey survey, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey cannot be null.");
        checkArgument(isNotBlank(survey.getGuid()), "Survey guid cannot be null or empty.");
        checkNotNull(survey.getCreatedOn(), "Survey createdOn cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        return post(clientProvider.getConfig().getSurveyUserApi(survey.getGuid(), survey.getCreatedOn()), answers, SimpleIdentifierHolder.class);
    }

    @Override
    public IdentifierHolder submitAnswersToSurvey(Survey survey, String identifier, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey cannot be null.");
        checkArgument(isNotBlank(survey.getGuid()), "Survey guid cannot be null or empty.");
        checkNotNull(survey.getCreatedOn(), "Survey createdOn cannot be null.");
        checkNotNull(identifier, "identifier cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        return post(clientProvider.getConfig().getSurveyWithIdentifierUserApi(survey.getGuid(), survey.getCreatedOn(),
                identifier), answers, SimpleIdentifierHolder.class);
    }
    
    @Override
    public SurveyResponse getSurveyResponse(String identifier) {
        session.checkSignedIn();
        checkArgument(isNotBlank(identifier), "Survey response identifier cannot be null or empty.");

        return get(clientProvider.getConfig().getSurveyResponseApi(identifier), SurveyResponse.class);
    }

    @Override
    public void addAnswersToResponse(SurveyResponse response, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(response, "Response cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        post(clientProvider.getConfig().getSurveyResponseApi(response.getIdentifier()), answers);
    }

    @Override
    public void deleteSurveyResponse(String identifier) {
        session.checkSignedIn();
        checkNotNull(isNotBlank(identifier), "Survey response identifier cannot be null or blank.");

        delete(clientProvider.getConfig().getSurveyResponseApi(identifier));
    }

    /*
     * Upload API
     */

    @Override
    public UploadSession requestUploadSession(UploadRequest request) {
        session.checkSignedIn();
        checkNotNull(request, "Request cannot be null.");

        return post(clientProvider.getConfig().getUploadApi(), request, UploadSession.class);
    }

    @Override
    public void upload(UploadSession session, UploadRequest request, String fileName) {
        this.session.checkSignedIn();
        checkNotNull(session, "session cannot be null.");
        checkNotNull(fileName, "fileName cannot be null.");
        checkArgument(session.getExpires().isAfter(DateTime.now()), "session already expired, cannot upload.");

        HttpEntity entity;
        try {
            byte[] b = Files.readAllBytes(Paths.get(fileName));
            entity = new ByteArrayEntity(b, ContentType.create(request.getContentType()));
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }
        String url = session.getUrl();
        s3Put(url, entity, request);

        post(clientProvider.getConfig().getUploadCompleteApi(session.getId()));
    }
}
