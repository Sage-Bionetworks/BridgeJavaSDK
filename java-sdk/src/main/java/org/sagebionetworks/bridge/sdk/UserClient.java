package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.SharingScope;
import org.sagebionetworks.bridge.sdk.models.accounts.Withdrawal;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduledActivity;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSession;
import org.sagebionetworks.bridge.sdk.models.upload.UploadValidationStatus;
import org.sagebionetworks.bridge.sdk.rest.api.ConsentsApi;
import org.sagebionetworks.bridge.sdk.rest.api.ParticipantsApi;
import org.sagebionetworks.bridge.sdk.rest.model.ConsentSignature;
import org.sagebionetworks.bridge.sdk.rest.model.EmptyPayload;
import org.sagebionetworks.bridge.sdk.rest.model.StudyParticipant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class UserClient extends BaseApiCaller {
    
    private final TypeReference<ResourceList<Schedule>> sType = new TypeReference<ResourceList<Schedule>>() {};
    
    private final TypeReference<ResourceList<ScheduledActivity>> saType = new TypeReference<ResourceList<ScheduledActivity>>() {};

    private final ParticipantsApi participantsApi;
    private final ConsentsApi consentsApi;
    UserClient(BridgeSession session) {
        super(session);
        this.participantsApi = API_CLIENT_PROVIDER.getClient(ParticipantsApi.class, signIn);
        this.consentsApi = API_CLIENT_PROVIDER.getClient(ConsentsApi.class, signIn);
    }

    /**
     * Get the StudyParticipant record for the currently authenticated user.
     * 
     * @return participant - the study participant record for this user
     */
    public StudyParticipant getStudyParticipant() {
        return call(participantsApi.getUsersParticipantRecord());
    }
    
    /**
     * Update the StudyParticipant associated with the currently authenticated user. 
     * Unlike other API calls, this call can take a sparse StudyParticipant object 
     * where only the fields you wish to update have been added to the object. Null 
     * fields will be ignored when updating the study participant's information (it 
     * will remain the same on the server).
     * 
     * @param participant
     *          The study participant object to update for this user; may be sparse 
     *          (only fill out fields you wish to update).
     */
    public void saveStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant, "StudyParticipant cannot be null.");

        UserSession userSession = post(config.getParticipantSelfApi(), participant, UserSession.class);
        session.setUserSession(userSession);
    }
    
    /**
     * Consent to research.
     *
     * @param subpopGuid
     *          The subpopulation of the study that the user is consenting to participate int.
     * @param signature
     *          Name, birthdate, and optionally signature image, of consenter's signature.
     * @param scope
     *          Scope of sharing for this consent
     */
    public void consentToResearch(SubpopulationGuid subpopGuid, org
            .sagebionetworks.bridge.sdk.models.accounts.ConsentSignature signature,
                                  SharingScope scope) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(signature, CANNOT_BE_NULL, "ConsentSignature");
        checkNotNull(scope, CANNOT_BE_NULL, "SharingScope");


        ConsentSubmission submission = new ConsentSubmission(signature, scope);
        
        post(config.getConsentSignatureApi(subpopGuid), submission);
        session.setSharingScope(scope);
        changeConsent(subpopGuid, true);
    }

    /**
     * Returns the user's consent signature, which includes the name, birthdate, and signature image.
     *
     * @param subpopGuid
     *      the GUID object of the subpopulation which the user consented to participate in
     * @return consent signature
     *      
     */
    public ConsentSignature getConsentSignature(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");

        return call(consentsApi.getConsentSignature(subpopGuid.getGuid()));
    }

    /**
     * Email the signed consent agreement to the participant's email address.
     * 
     * @param subpopGuid
     *      email the consent to the user for the GUID object of the subpopulation the user consented to 
     */
    public void emailConsentSignature(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        call(consentsApi.emailConsentAgreement(subpopGuid.getGuid(), new EmptyPayload()));
    }

    /**
     * Withdraw user's consent to participate in research. The user will no longer be able to submit 
     * data to the server without receiving an error response from the server (ConsentRequiredException).
     * @param subpopGuid
     *      The subpopulation (consent group) from which to withdraw.
     * @param reason
     *      The reason for withdrawing (will be emailed to a study administrator). Optional
     */
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
    
    /**
     * Withdraw from all signed consents, even from subpopulations that may no longer be visible to 
     * the user given their current application version, language, data group tags, etc. This is 
     * method is guaranteed to withdraw the user from the study even if the user can no longer 
     * access every subpopulation to which they have consented. 
     *  
     * @param reason
     *      The reason for withdrawing (will be emailed to a study administrator). Optional
     */
    public void withdrawAllConsentsToResearch(String reason) {
        session.checkSignedIn();
        
        Withdrawal withdrawal = new Withdrawal(reason);
        post(config.getConsentsWithdrawApi(), withdrawal);
        session.setSharingScope(SharingScope.NO_SHARING); 
        changeAllConsents(false);
    }
    
    /**
     * Get all schedules associated with a study.
     *
     * @return
     *      a list of all schedules associated with the current study
     */
    public ResourceList<Schedule> getSchedules() {
        session.checkSignedIn();
        return get(config.getSchedulesApi(), sType);
    }

    /**
     * Get a survey version with a GUID and a createdOn timestamp.
     *
     * @param keys
     *      the survey key object (GUID and createdOn timestamp) for the survey to retrieve
     * @return
     *      the survey with the given GUID and createdOn timestamp
     */
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    /**
     * Get the most recently published survey available for the provided survey GUID.
     * @param guid
     *      the GUID of the survey
     * @return
     *      the most recently published version of the survey with the given GUID
     */
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkNotNull(guid, CANNOT_BE_NULL, "guid");
        
        return get(config.getRecentlyPublishedSurveyForUserApi(guid), Survey.class);
    }

    /**
     * Request an upload session from the user.
     *
     * @param request
     *      the request object Bridge uses to create the Upload Session.
     * @return
     *      an upload session for this upload
     */
    public UploadSession requestUploadSession(UploadRequest request) {
        session.checkSignedIn();
        checkNotNull(request, "Request cannot be null.");

        return post(config.getUploadsApi(), request, UploadSession.class);
    }

    /**
     * Upload a file using the requested UploadSession. Closes the upload after it's done.
     *
     * @param session
     *      The session used to upload.
     * @param request
     *      the upload request
     * @param fileName
     *      file to upload
     */
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

    /**
     * Gets the upload status (status and validation messages) for the given upload ID
     *
     * @param uploadId
     *         ID of the upload, obtained from the upload session
     * @return object containing upload status and validation messages
     */
    public UploadValidationStatus getUploadStatus(String uploadId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(uploadId), CANNOT_BE_BLANK, "uploadId");
        return get(config.getUploadStatusApi(uploadId), UploadValidationStatus.class);
    }

    /**
     * Get the list of available or scheduled activities.
     * @param daysAhead
     *      return activities from now until the number of days ahead from now (maximum of 4 days)
     * @param timeZone
     *      the timezone the activities should use when returning scheduledOn and expiresOn dates
     * @param minimumPerSchedule
     *      Optional. If set, API will return either N days of tasks, or N task per schedule, whichever is highest. 
     *      Current maximum for this value is 5.
     * @return
     *      a list of scheduled activities that meet the query criteria
     */
    public ResourceList<ScheduledActivity> getScheduledActivities(int daysAhead, DateTimeZone timeZone, Integer minimumPerSchedule) {
        session.checkSignedIn();

        return get(config.getScheduledActivitiesApi(daysAhead, timeZone, minimumPerSchedule), saType);
    }
    
    /**
     * Update these activities (by setting either the startedOn or finishedOn values of each activity). 
     * The only other required value that must be set for the activity is its GUID.
     * @param scheduledActivities
     *      a list of the scheduled activities to update (new startedOn or finishedOn timestamps)
     */
    public void updateScheduledActivities(List<ScheduledActivity> scheduledActivities) {
        checkNotNull(scheduledActivities);
        session.checkSignedIn();
        post(config.getScheduledActivitiesApi(), scheduledActivities);
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
    
    private void changeAllConsents(boolean isConsented) {
        ImmutableMap.Builder<SubpopulationGuid,ConsentStatus> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<SubpopulationGuid, ConsentStatus> entry : session.getConsentStatuses().entrySet()) {
            SubpopulationGuid guid = entry.getKey();
            ConsentStatus status = entry.getValue();
            builder.put(guid, new ConsentStatus(status.getName(), status.getSubpopulationGuid(), 
                    status.isRequired(), isConsented, status.isMostRecentConsent()));
        }
        session.setConsentStatuses(builder.build());
    }
    
}
