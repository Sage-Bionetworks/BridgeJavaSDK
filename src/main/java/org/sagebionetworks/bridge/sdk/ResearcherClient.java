package org.sagebionetworks.bridge.sdk;

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

}
