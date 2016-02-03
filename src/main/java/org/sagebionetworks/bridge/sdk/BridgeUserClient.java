package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleIdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduledActivity;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.upload.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSession;
import org.sagebionetworks.bridge.sdk.models.upload.UploadValidationStatus;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.ExternalIdentifier;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;
import org.sagebionetworks.bridge.sdk.models.users.Withdrawal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

class BridgeUserClient extends BaseApiCaller implements UserClient {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("ZZ");
    
    private final TypeReference<ResourceListImpl<Schedule>> sType = new TypeReference<ResourceListImpl<Schedule>>() {};
    
    private final TypeReference<ResourceListImpl<ScheduledActivity>> saType = new TypeReference<ResourceListImpl<ScheduledActivity>>() {};

    BridgeUserClient(BridgeSession session) {
        super(session);
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
    }
    
    @Override
    public void addExternalUserIdentifier(ExternalIdentifier identifier) {
        session.checkSignedIn();
        checkNotNull(identifier, CANNOT_BE_NULL, "ExternalIdentifier");
        
        post(config.getSetExternalIdApi(), identifier);
    }

    /*
     * Consent API
     */
    @Override
    public void consentToResearch(SubpopulationGuid subpopGuid, ConsentSignature signature, SharingScope scope) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(signature, CANNOT_BE_NULL, "ConsentSignature");
        checkNotNull(scope, CANNOT_BE_NULL, "SharingScope");

        ConsentSubmission submission = new ConsentSubmission(signature, scope);
        
        post(config.getConsentSignatureApi(subpopGuid), submission);
        session.setSharingScope(scope);
        changeConsent(subpopGuid, true);
    }

    @Override
    public ConsentSignature getConsentSignature(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        ConsentSignature sig = get(config.getConsentSignatureApi(subpopGuid), ConsentSignature.class);
        return sig;
    }

    @Override
    public void emailConsentSignature(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        post(config.getEmailConsentSignatureApi(subpopGuid));
    }

    @Override
    public void changeSharingScope(SharingScope sharingScope) {
        session.checkSignedIn();
        checkNotNull(sharingScope, CANNOT_BE_NULL, "SharingScope");
        
        ScopeOption option = new ScopeOption(sharingScope);
        post(config.getSetDataSharingApi(), option);
        session.setSharingScope(sharingScope);
    }

    @Override
    public void withdrawConsentToResearch(SubpopulationGuid subpopGuid, String reason) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        Withdrawal withdrawal = new Withdrawal(reason);
        post(config.getWithdrawConsentSignatureApi(subpopGuid), withdrawal);
        // alxdark (12/18/2015): TODO: implement the same logic as is on the server to determine
        // when we should do this? Right now this might not be accurate for multiple consents, 
        // although no studies exist in this configuration at this time.
        session.setSharingScope(SharingScope.NO_SHARING); 
        changeConsent(subpopGuid, false);
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
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    @Override
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkNotNull(guid, CANNOT_BE_NULL, "guid");
        
        return get(config.getRecentlyPublishedSurveyForUserApi(guid), Survey.class);
    }
    
    @Override
    public IdentifierHolder submitAnswersToSurvey(GuidCreatedOnVersionHolder keys, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(keys, "Survey keys cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        SurveyResponseSubmit response = new SurveyResponseSubmit(keys, null, answers);
        return post(config.getSurveyResponsesApi(), response, SimpleIdentifierHolder.class);
    }

    @Override
    public IdentifierHolder submitAnswersToSurvey(GuidCreatedOnVersionHolder keys, String identifier, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkNotNull(keys, "GuidCreatedOnVersionHolder cannot be null.");
        checkNotNull(identifier, "identifier cannot be null.");
        checkNotNull(answers, "Answers cannot be null.");

        SurveyResponseSubmit response = new SurveyResponseSubmit(keys, identifier, answers);
        return post(config.getSurveyResponsesApi(), response, SimpleIdentifierHolder.class);
    }
    
    @Override
    public SurveyResponse getSurveyResponse(String identifier) {
        session.checkSignedIn();
        checkArgument(isNotBlank(identifier), "Survey response identifier cannot be null or empty.");

        return get(config.getSurveyResponseApi(identifier), SurveyResponse.class);
    }

    @Override
    public void addAnswersToResponse(String identifier, List<SurveyAnswer> answers) {
        session.checkSignedIn();
        checkArgument(isNotBlank(identifier), "Identifier cannot be null or empty.");
        checkNotNull(answers, "Answers cannot be null.");
        
        SurveyResponseSubmit res = new SurveyResponseSubmit(null, identifier, answers);
        post(config.getSurveyResponseApi(identifier), res);
    }

    /*
     * Upload API
     */
    @Override
    public UploadSession requestUploadSession(UploadRequest request) {
        session.checkSignedIn();
        checkNotNull(request, "Request cannot be null.");

        return post(config.getUploadsApi(), request, UploadSession.class);
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
            throw new BridgeSDKException(e.getMessage(), e, config.getCompleteUploadApi(session.getId()));
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e, config.getCompleteUploadApi(session.getId()));
        }
        String url = session.getUrl().toString();
        s3Put(url, entity, request);
        
        post(config.getCompleteUploadApi(session.getId()));
    }

    @Override
    public UploadValidationStatus getUploadStatus(String uploadId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(uploadId), CANNOT_BE_BLANK, "uploadId");
        return get(config.getUploadStatusApi(uploadId), UploadValidationStatus.class);
    }

    @Override
    public ResourceList<ScheduledActivity> getScheduledActivities(int daysAhead, DateTimeZone timeZone) {
        session.checkSignedIn();
        try {
            String offsetString = DATETIME_FORMATTER.withZone(timeZone).print(0);
            String queryString = "?daysAhead=" + Integer.toString(daysAhead) + "&offset="
                            + URLEncoder.encode(offsetString, "UTF-8");
            return get(config.getScheduledActivitiesApi() + queryString, saType);
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateScheduledActivities(List<ScheduledActivity> scheduledActivities) {
        checkNotNull(scheduledActivities);
        session.checkSignedIn();
        post(config.getScheduledActivitiesApi(), scheduledActivities);
    }
    
    @Override
    public void updateDataGroups(DataGroups dataGroups) {
        checkNotNull(dataGroups);
        checkNotNull(dataGroups.getDataGroups());
        session.checkSignedIn();
        
        post(config.getDataGroupsApi(), dataGroups);
    }
    
    @Override
    public DataGroups getDataGroups() {
        session.checkSignedIn();
        
        return get(config.getDataGroupsApi(), DataGroups.class);
    }
    
    private void changeConsent(SubpopulationGuid subpopGuid, boolean isConsented) {
        ImmutableMap.Builder<SubpopulationGuid,ConsentStatus> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<SubpopulationGuid, ConsentStatus> entry : session.getConsentStatuses().entrySet()) {
            SubpopulationGuid guid = entry.getKey();
            ConsentStatus status = entry.getValue();
            if (status.getSubpopulationGuid().equals(subpopGuid.getGuid())) {
                builder.put(guid, new ConsentStatus(status.getName(), status.getSubpopulationGuid(), 
                        status.isRequired(), isConsented, status.isMostRecentConsent()));
            } else {
                builder.put(guid, status);
            }
        }
        session.setConsentStatuses(builder.build());
    }
    
}
