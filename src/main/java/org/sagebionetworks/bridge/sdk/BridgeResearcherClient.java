package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {
    
    private final TypeReference<ResourceListImpl<StudyConsent>> scType = new TypeReference<ResourceListImpl<StudyConsent>>() {};

    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    @Override
    public ResourceList<StudyConsent> getAllStudyConsents(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");

        return get(config.getConsentsApi(subpopGuid), scType);
    }
    @Override
    public StudyConsent getPublishedStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getPublishedStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    @Override
    public StudyConsent getMostRecentStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getMostRecentStudyConsentApi(subpopGuid), StudyConsent.class);
    };
    @Override
    public StudyConsent getStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return get(config.getConsentApi(subpopGuid, createdOn), StudyConsent.class);
    }
    @Override
    public void createStudyConsent(SubpopulationGuid subpopGuid, StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        post(config.getConsentsApi(subpopGuid), consent, StudyConsent.class);
    }
    @Override
    public void publishStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        post(config.getPublishStudyConsentApi(subpopGuid, createdOn));
    }
    @Override
    public void sendStudyParticipantsRoster() {
        session.checkSignedIn();
        
        post(config.getEmailParticipantRosterApi());
    }

}
