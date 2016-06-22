package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.accounts.Withdrawal;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.type.TypeReference;

public class ParticipantClient extends BaseApiCaller {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantClient.class);
    
    private static final TypeReference<PagedResourceList<AccountSummary>> ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<AccountSummary>>() {};

    ParticipantClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Retrieve a page of participant summaries (name and email).
     * @param offsetBy
     *      number of records to offset the page of records returned
     * @param pageSize
     *      the size of the page (must be from 5-250 records).
     * @param emailFilter
     *      an optional substring that will be matched (case insensitive) against the email addresses 
     *      of participant accounts that are returned from this search. Neither null nor an empty 
     *      string will filter results.
     * @return
     */
    public PagedResourceList<AccountSummary> getPagedAccountSummaries(int offsetBy, int pageSize, String emailFilter) {
        session.checkSignedIn();
        
        return get(config.getParticipantsApi(offsetBy, pageSize, emailFilter), ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST);
    }
    
    /**
     * Get an individual participant account. This can include any user, even ones who have not 
     * enrolled in the study (they have not verified their email address, they have not signed the 
     * required consents, etc.). However, all the information about participation in a study is 
     * included in the StudyParticipant, including a full history of consent and withdrawal if it
     * exists.
     * @param userId
     *      the user's id
     * @return
     */
    public StudyParticipant getStudyParticipant(String userId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");

        return get(config.getParticipantApi(userId), StudyParticipant.class);
    }
    
    /**
     * Create a study participant.
     * 
     * @param participant
     */
    public IdentifierHolder createStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        
        return post(config.getParticipantsApi(), participant, IdentifierHolder.class);
    }
    
    /**
     * Update an individual study participant. Not all records in study participant can be changed (some 
     * are readonly), and this is reflected in the fields that can be set from the StudyParticipant.Builder.
     * @param participant
     *      The participant object. The update will be made based on all the values that can be set through 
     *      the StudyParticipant.Builder.
     */
    public void updateStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        checkArgument(isNotBlank(participant.getId()), CANNOT_BE_BLANK, "id");
        
        post(config.getParticipantApi(participant.getId()), participant);

        // User is signed out if they edit their own StudyParticipant through this API.
        if (session.getStudyParticipant().getId().equals(participant.getId())) {
            LOG.warn("Client edited self through researcher participant API, session has been signed out. Sign back in to update your session.");
            session.removeSession();
        }
    }
    
    /**
     * Sign out a user's session.
     *
     * @param email
     *            Email address identifying the user to sign out.
     */
    public void signOutUser(String email) {
        session.checkSignedIn();

        post(config.getUsersSignOutApi(email));
    }
    
    /**
     * Trigger an email to the user with the given ID, that includes instructions on how they can reset their 
     * password. 
     * 
     * @param userId
     *      the user's id
     */
    public void requestResetPassword(String userId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");

        post(config.getParticipantRequestResetPasswordApi(userId));
    }
    
    /**
     * Resend and email verification request to an account that is unverified.
     *  
     * @param userId
     *      the user's id
     */
    public void resendEmailVerification(String userId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");

        post(config.getParticipantResendEmailVerificationApi(userId));
    }
    
    /**
     * Resend the signed consent agreement via email to a study participant. The subpopulation (consent group) of the
     * consent must be specified since a participant may have signed more than one consent.
     * 
     * @param userId
     *      the user's id
     * @param subpopGuid
     */
    public void resendConsentAgreement(String userId, SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        post(config.getParticipantResendConsentApi(userId, subpopGuid));
    }
    
    /**
     * Withdraw the participant from any consents that they have signed, even from historical subpopulations 
     * that may no longer be applicable to the user given their current application version, language, data 
     * group tags, etc. This is method is guaranteed to withdraw the user from the study even if the user 
     * can no longer access every subpopulation to which they have consented.
     *  
     * @param userId
     *      the user's id
     * @param reason
     *      The reason for withdrawing (will be emailed to a study administrator). Optional
     */
    public void withdrawAllConsentsToResearch(String userId, String reason) {
        session.checkSignedIn();
        checkArgument(isNotBlank(userId), CANNOT_BE_BLANK, "userId");
        
        Withdrawal withdrawal = new Withdrawal(reason);
        post(config.getParticipantConsentsWithdrawApi(userId), withdrawal);
    }
}
