package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

public interface ResearcherClient {

    // STUDY CONSENTS
    /**
     * Get all revisions of the consent document for the study of the current researcher.
     * @param subpopGuid
     * @return List<StudyConsent>
     */
    public ResourceList<StudyConsent> getAllStudyConsents(SubpopulationGuid subpopGuid);

    /**
     * Get the published consent document revision. This is the revision that is sent to users, and should be a
     * version of the consent that has been approved by your IRB. Only one revision of your consent is published 
     * at any given time.
     * @param subpopGuid
     * @return StudyConsent
     */
    public StudyConsent getPublishedStudyConsent(SubpopulationGuid subpopGuid);

    /**
     * Get the most recent revision of the consent document.
     * @param subpopGuid
     * @return StudyConsent
     */
    public StudyConsent getMostRecentStudyConsent(SubpopulationGuid subpopGuid);

    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param subpopGuid
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public StudyConsent getStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn);

    /**
     * Create a consent document revision.
     * @param subpopGuid
     * @param consent
     *            The consent document to add.
     */
    public void createStudyConsent(SubpopulationGuid subpopGuid, StudyConsent consent);

    /**
     * Publish a consent document created at a DateTime. The prior published revision will no longer be published.
     * @param subpopGuid
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void publishStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn);

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
