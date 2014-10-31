package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.ConsentDocument;

public interface AdminClient {

    /**
     * Get all consent documents associated with the study currently signed into.
     *
     * @return List<StudyConsent>
     */
    public List<ConsentDocument> getAllConsentDocuments();

    /**
     * Get the most recently activated consent document. Can have more than one concurrently active consent document,
     * and this method retrieves only the most recent.
     *
     * @return StudyConsent
     */
    public ConsentDocument getMostRecentlyActivatedConsentDocument();

    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public ConsentDocument getConsentDocument(DateTime createdOn);

    /**
     * Add a consent document to the study.
     *
     * @param consent
     *            The consent document to add.
     */
    public void addConsentDocument(ConsentDocument consent);

    /**
     * Activate a consent document created at a DateTime. Can have more than one concurrently active consent document.
     *
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void activateConsentDocument(DateTime createdOn);

    /**
     * Create a user.
     *
     * @param signUp
     *            Credentials used to sign up the user into Bridge.
     * @param roles
     *            Roles assigned to the user ("studykey_researcher", "studykey_admin", or null for participants).
     * @param consent
     *            Whether the user should be automatically consented upon sign up.
     * @return true if success, false if failure.
     */
    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent);

    /**
     * Delete a user.
     *
     * @param email
     *            Email address identifying the user to delete.
     * @return true if success, false if failure.
     */
    public boolean deleteUser(String email);

}
