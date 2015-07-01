package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {
    
    private final TypeReference<ResourceListImpl<StudyConsent>> scType = new TypeReference<ResourceListImpl<StudyConsent>>() {};

    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    @Override
    public ResourceList<StudyConsent> getAllStudyConsents() {
        session.checkSignedIn();

        return get(config.getStudyConsentsApi(), scType);
    }
    @Override
    public StudyConsent getActiveStudyConsent() {
        session.checkSignedIn();

        return get(config.getActiveStudyConsentApi(), StudyConsent.class);
    }
    @Override
    public StudyConsent getMostRecentStudyConsent() {
        session.checkSignedIn();

        return get(config.getMostRecentStudyConsentApi(), StudyConsent.class);
    };
    @Override
    public StudyConsent getStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        return get(config.getStudyConsentApi(createdOn), StudyConsent.class);
    }
    @Override
    public void createStudyConsent(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, Bridge.CANNOT_BE_NULL, "consent");

        post(config.getStudyConsentsApi(), consent, StudyConsent.class);
    }
    @Override
    public void activateStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        post(config.getVersionStudyConsentApi(createdOn));
    }
    @Override
    public void sendStudyParticipantsRoster() {
        session.checkSignedIn();
        
        post(config.getResearcherStudyParticipantsApi());
    }

}
