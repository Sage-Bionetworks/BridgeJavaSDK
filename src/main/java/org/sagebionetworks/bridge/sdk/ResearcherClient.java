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
     * Get the activate consent document revision. This is the published revision that is sent to users, and thus a
     * version of the consent that has been approved by your IRB. There is only one consent revision active at any given
     * time.
     *
     * @return StudyConsent
     */
    public StudyConsent getActiveStudyConsent();

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
     * Activate a consent document created at a DateTime. This will de-activate the previously active revision.
     *
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void activateStudyConsent(DateTime createdOn);

    /**
     * Email a list of users who have signed consents to participate in the researcher's study. The email is sent to the
     * study's consent notification email contact. The list is sorted by the participants' email addresses.
     * 
     * @return
     */
    public void sendStudyParticipantsRoster();

}
