package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.type.TypeReference;

public class StudyConsentClient extends BaseApiCaller {
    
    private final TypeReference<ResourceList<StudyConsent>> STUDY_CONSENT_LIST_TYPE = new TypeReference<ResourceList<StudyConsent>>() {};
    
    StudyConsentClient(BridgeSession session) {
        super(session);
    }

    /**
     * Get all revisions of the consent document for the study of the current researcher.
     * @param subpopGuid
     *      The GUID key object of the subpopulation 
     * @return
     *      A list of all revisions of the consent document for this subpopulation
     */
    public ResourceList<StudyConsent> getAllStudyConsents(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");

        return get(config.getConsentsApi(subpopGuid), STUDY_CONSENT_LIST_TYPE);
    }
    
    /**
     * Get the published consent document revision. This is the revision that is sent to users, and should be a
     * version of the consent that has been approved by your IRB. Only one revision of your consent is published 
     * at any given time.
     * @param subpopGuid
     *      The GUID key object of the subpopulation
     * @return
     *      The published version of the consent for this subpopulation
     */
    public StudyConsent getPublishedStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getPublishedStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    
    /**
     * Get the most recent revision of the consent document.
     * @param subpopGuid
     *      The GUID key object of the subpopulation
     * @return
     *      The most recent version of the consent for this subpopulation
     */
    public StudyConsent getMostRecentStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getMostRecentStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    
    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param subpopGuid
     *      The GUID key object of the subpopulation
     * @param createdOn
     *      The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return
     *      the study consent created on a specific revision date
     */
    public StudyConsent getStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return get(config.getConsentApi(subpopGuid, createdOn), StudyConsent.class);
    }
    
    /**
     * Create a consent document revision.
     * @param subpopGuid
     *      The GUID key object of the subpopulation
     * @param consent
     *      The consent document to add.
     */
    public void createStudyConsent(SubpopulationGuid subpopGuid, StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        post(config.getConsentsApi(subpopGuid), consent, StudyConsent.class);
    }
    
    /**
     * Publish a consent document created at a DateTime. The prior published revision will no longer be published.
     * @param subpopGuid
     *      The GUID key object of the subpopulation
     * @param createdOn
     *      DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void publishStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        post(config.getPublishStudyConsentApi(subpopGuid, createdOn));
    }
}
