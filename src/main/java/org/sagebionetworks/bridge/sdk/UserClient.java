package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
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
     *            Name, birthdate, and optionally signature image, of consenter's signature.
     * @param sharingScope
     *            The scope of sharing allowed (initially) by this consent.
     */
    public void consentToResearch(ConsentSignature signature, SharingScope sharingScope);

    /**
     * Returns the user's consent signature, which includes the name, birthdate, and signature image.
     *
     * @return consent signature
     */
    public ConsentSignature getConsentSignature();

    /**
     * Change (stop, resume) the sharing of data for this participant.
     * @param sharing
     */
    public void changeSharingScope(SharingScope sharing);
    
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
    public IdentifierHolder submitAnswersToSurvey(GuidCreatedOnVersionHolder keys, List<SurveyAnswer> answers);

    /**
     * Submit a list of SurveyAnswers to a particular survey, using a specified identifier
     * for the survey response (the value should be a unique string, like a GUID, that 
     * has not been used for any prior submissions).
     *
     * @param survey
     *            The survey that the answers will be added to.
     * @param identifier
     *            A unique string to identify this set of survey answers as originating
     *            from the same run of a survey
     * @param answers
     *            The answers to add to the survey.
     * @return IdentifierHolder A holder storing the GUID of the survey.
     */
    public IdentifierHolder submitAnswersToSurvey(Survey survey, String identifier, List<SurveyAnswer> answers);
    
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
     * @param identifier
     *            The identifier of the survey response to delete.
     */
    public void deleteSurveyResponse(String identifier);

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
