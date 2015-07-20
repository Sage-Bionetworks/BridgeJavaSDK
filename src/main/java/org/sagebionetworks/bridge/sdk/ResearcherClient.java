package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;

public interface ResearcherClient {

    // STUDY CONSENTS
    /**
     * Get all revisions of the consent document for the study of the current researcher.
     *
     * @return List<StudyConsent>
     */
    public ResourceList<StudyConsent> getAllStudyConsents();

    /**
     * Get the published consent document revision. This is the revision that is sent to users, and should be a
     * version of the consent that has been approved by your IRB. Only one revision of your consent is published 
     * at any given time.
     *
     * @return StudyConsent
     */
    public StudyConsent getPublishedStudyConsent();

    /**
     * Get the most recent revision of the consent document.
     * 
     * @return StudyConsent
     */
    public StudyConsent getMostRecentStudyConsent();

    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public StudyConsent getStudyConsent(DateTime createdOn);

    /**
     * Create a consent document revision.
     *
     * @param consent
     *            The consent document to add.
     */
    public void createStudyConsent(StudyConsent consent);

    /**
     * Publish a consent document created at a DateTime. The prior published revision will no longer be published.
     *
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void publishStudyConsent(DateTime createdOn);

    /**
     * Email a list of users who have signed consents to participate in the researcher's study. The email is sent to the
     * study's consent notification email contact. The list is sorted by the participants' email addresses.
     * 
     * @return
     */
    public void sendStudyParticipantsRoster();

}
