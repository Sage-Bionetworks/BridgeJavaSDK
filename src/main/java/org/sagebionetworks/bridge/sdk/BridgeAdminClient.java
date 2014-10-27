package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public final class BridgeAdminClient {

    private static final String MUST_AUTHENTICATE = "Provider must be signed in.";
    private static final String CANNOT_BE_NULL = "{0} cannot be null.";
    
    private final ClientProvider provider;
    private final StudyConsentApiCaller studyConsentApi;
    private final UserManagementApiCaller userManagementApi;

    private BridgeAdminClient(ClientProvider provider) {
        this.provider = provider;
        this.studyConsentApi = StudyConsentApiCaller.valueOf(provider);
        this.userManagementApi = UserManagementApiCaller.valueOf(provider);
    }

    static BridgeAdminClient valueOf(ClientProvider provider) {
        return new BridgeAdminClient(provider);
    }
    
    public List<StudyConsent> getAllConsentDocuments() {
        checkState(provider.isSignedIn(), MUST_AUTHENTICATE);

        return studyConsentApi.getAllStudyConsents();
    }

    public StudyConsent getActiveConsentDocument() {
        checkState(provider.isSignedIn(), MUST_AUTHENTICATE);

        return studyConsentApi.getActiveStudyConsent();
    }

    public StudyConsent getConsentDocument(DateTime createdOn) {
        checkState(provider.isSignedIn(), MUST_AUTHENTICATE);
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return studyConsentApi.getStudyConsent(createdOn);
    }

    public void addConsentDocument(StudyConsent consent) {
        checkState(provider.isSignedIn(), MUST_AUTHENTICATE);
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        studyConsentApi.addStudyConsent(consent);
    }

    public void activateConsentDocument(DateTime createdOn) {
        checkState(provider.isSignedIn(), MUST_AUTHENTICATE);
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        studyConsentApi.setActiveStudyConsent(createdOn);
    }

    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent) {
        return userManagementApi.createUser(signUp, roles, consent);
    }

    public boolean deleteUser(String email) {
        return userManagementApi.deleteUser(email);
    }

}
