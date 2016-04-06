package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

public interface ResearcherClient {

    /**
     * Sign out a user's session.
     *
     * @param email
     *            Email address identifying the user to sign out.
     */
    public void signOutUser(String email);

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
    PagedResourceList<AccountSummary> getPagedAccountSummaries(int offsetBy, int pageSize, String emailFilter);

    /**
     * Get an individual participant account. This can include any user, even ones who have not 
     * enrolled in the study (they have not verified their email address, they have not signed the 
     * required consents, etc.). However, all the information about participation in a study is 
     * included in the StudyParticipant, including a full history of consent and withdrawal if it
     * exists.
     * @param email
     *      the user's email
     * @return
     */
    StudyParticipant getStudyParticipant(String email);
    
    /**
     * Update an individual study participant. Not all records in study participant can be changed (some 
     * are readonly), and this is reflected in the fields that can be set from the StudyParticipant.Builder.
     * @param participant
     *      The participant object. The update will be made based on all the values that can be set through 
     *      the StudyParticipant.Builder.
     */
    void updateStudyParticipant(StudyParticipant participant);
    
    /**
     * Create a study participant.
     * 
     * @param participant
     */
    void createStudyParticipant(StudyParticipant participant);
}
