package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public final class BridgeAdminClient {

    private final ClientProvider provider;
    private final StudyConsentApiCaller studyConsentApi;

    private BridgeAdminClient(ClientProvider provider) {
        this.provider = provider;
        this.studyConsentApi = StudyConsentApiCaller.valueOf(provider);
    }

    static BridgeAdminClient valueOf(ClientProvider provider) {
        return new BridgeAdminClient(provider);
    }

    public List<StudyConsent> getAllConsentDocuments() {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in.");

        return studyConsentApi.getAllStudyConsents();
    }

    public StudyConsent getActiveConsentDocument() {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in.");

        return studyConsentApi.getActiveStudyConsent();
    }

    public StudyConsent getConsentDocument(DateTime createdOn) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in.");
        Preconditions.checkNotNull(createdOn, "createdOn cannot be null.");

        return studyConsentApi.getStudyConsent(createdOn);
    }

    public void addConsentDocument(StudyConsent consent) {
        Preconditions.checkState(provider.isSignedIn(), "Provider ust be signed in.");
        Preconditions.checkNotNull(consent, "consent cannot be null.");

        studyConsentApi.addStudyConsent(consent);
    }

    public void activateConsentDocument(DateTime createdOn) {
        Preconditions.checkState(provider.isSignedIn(), "Provider must be signed in.");
        Preconditions.checkNotNull(createdOn, "createdOn cannot be null.");

        studyConsentApi.setActiveStudyConsent(createdOn);
    }

}
