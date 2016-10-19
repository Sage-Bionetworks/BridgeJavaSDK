package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;

import com.fasterxml.jackson.core.type.TypeReference;

public class ResearcherClient extends StudyStaffClient {

    private static final Logger logger = LoggerFactory.getLogger(ResearcherClient.class);
    
    private static final TypeReference<PagedResourceList<AccountSummary>> ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<AccountSummary>>() {};
                    
    ResearcherClient(BridgeSession session) {
        super(session);
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
     *      The list of accounts that meet the query criteria
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
     * @param id
     *      the user's id
     * @return
     *      the study participant record
     */
    public StudyParticipant getStudyParticipant(String id) {
        session.checkSignedIn();
        checkArgument(isNotBlank(id), CANNOT_BE_BLANK, "id");

        return get(config.getParticipantApi(id), StudyParticipant.class);
    }
    
    /**
     * Create a study participant.
     * 
     * @param participant
     *      A study participant to create
     * @return
     *      The identifier assigned to the newly created participant
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
            logger.warn("Client edited self through researcher participant API, session has been signed out. Sign back in to update your session.");
            session.removeSession();
        }
    }
    
    /**
     * Trigger an email to the user with the given ID, that includes instructions on how they can reset their 
     * password. 
     * 
     * @param id
     *      Send a reset password email to the user with the given ID
     */
    public void requestResetPassword(String id) {
        session.checkSignedIn();
        checkArgument(isNotBlank(id), CANNOT_BE_BLANK, "id");

        post(config.getParticipantRequestResetPasswordApi(id));
    }
}
