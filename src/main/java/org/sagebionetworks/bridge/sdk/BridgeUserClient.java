package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleIdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.Task;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.upload.UploadValidationStatus;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.ExternalIdentifier;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeUserClient extends BaseApiCaller implements UserClient {

    private final TypeReference<ResourceListImpl<Schedule>> sType = new TypeReference<ResourceListImpl<Schedule>>() {};
    
    private final TypeReference<ResourceListImpl<Task>> tType = new TypeReference<ResourceListImpl<Task>>() {};

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
        session.setUsername(profile.getUsername());
    }
    
    @Override
    public void addExternalUserIdentifier(ExternalIdentifier identifier) {
        session.checkSignedIn();
        checkNotNull(identifier, Bridge.CANNOT_BE_NULL, "ExternalIdentifier");
        
        post(config.geSetExternalIdApi(), identifier);
    }

    /*
     * Consent API
     */

    @Override
    public void consentToResearch(ConsentSignature signature, SharingScope scope) {
        session.checkSignedIn();
        checkNotNull(signature, Bridge.CANNOT_BE_NULL, "ConsentSignature");
        checkNotNull(scope, Bridge.CANNOT_BE_NULL, "SharingScope");

        ConsentSubmission submission = new ConsentSubmission(signature, scope);
        
        post(config.getConsentSignatureApi(), submission);
        session.setConsented(true);
        session.setSharingScope(scope);
    }

    @Override
    public ConsentSignature getConsentSignature() {
        session.checkSignedIn();
        ConsentSignature sig = get(config.getConsentSignatureApi(), ConsentSignature.class);
        return sig;
    }

    @Override
    public void emailConsentSignature() {
        session.checkSignedIn();
        post(config.getEmailConsentSignatureApi());
    }

    @Override
    public void changeSharingScope(SharingScope sharingScope) {
        session.checkSignedIn();
        checkNotNull(sharingScope, Bridge.CANNOT_BE_NULL, "SharingScope");
        
        ScopeOption option = new ScopeOption(sharingScope);
        post(config.getSetDataSharingApi(), option);
        session.setSharingScope(sharingScope);
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

        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    @Override
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkNotNull(guid, Bridge.CANNOT_BE_NULL, "guid");
        
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
            throw new BridgeSDKException(e);
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }
        String url = session.getUrl().toString();
        s3Put(url, entity, request);
        
        post(config.getCompleteUploadApi(session.getId()));
    }

    @Override
    public UploadValidationStatus getUploadStatus(String uploadId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(uploadId), Bridge.CANNOT_BE_BLANK, "uploadId");
        return get(config.getUploadStatusApi(uploadId), UploadValidationStatus.class);
    }

    @Override
    public ResourceList<Task> getTasks(DateTime until) {
        session.checkSignedIn();
        
        String queryString = (until == null) ? "" : "?until="+until.toString();
        return get(config.getTasksApi() + queryString, tType);
    }

    @Override
    public void updateTasks(List<Task> tasks) {
        checkNotNull(tasks);
        session.checkSignedIn();
        post(config.getTasksApi(), tasks);
    }
}
