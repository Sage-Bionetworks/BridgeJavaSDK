package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

public interface ResearcherClient {

    /**
     * Email a list of users who have signed consents to participate in the researcher's study. The email is sent to the
     * study's consent notification email contact. The list is sorted by the participants' email addresses.
     * 
     * @return
     */
    public void sendStudyParticipantsRoster();

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
     * @return
     */
    PagedResourceList<AccountSummary> getPagedAccountSummaries(int offsetBy, int pageSize);
    
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
}
