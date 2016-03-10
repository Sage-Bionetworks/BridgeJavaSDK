package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;

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
}
