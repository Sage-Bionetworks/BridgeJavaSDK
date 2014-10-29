package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

final class BridgeAdminClient implements AdminClient {

    private static final String CANNOT_BE_NULL = "{0} cannot be null.";

    private final Session session;
    private final StudyConsentApiCaller studyConsentApi;
    private final UserManagementApiCaller userManagementApi;

    private BridgeAdminClient(Session session) {
        this.session = session;
        this.studyConsentApi = StudyConsentApiCaller.valueOf(session);
        this.userManagementApi = UserManagementApiCaller.valueOf(session);
    }

    static BridgeAdminClient valueOf(Session session) {
        return new BridgeAdminClient(session);
    }

    @Override
    public List<StudyConsent> getAllConsentDocuments() {
        session.checkSignedIn();

        return studyConsentApi.getAllStudyConsents();
    }
    @Override
    public StudyConsent getMostRecentlyActivatedConsentDocument() {
        session.checkSignedIn();

        return studyConsentApi.getActiveStudyConsent();
    }
    @Override
    public StudyConsent getConsentDocument(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return studyConsentApi.getStudyConsent(createdOn);
    }
    @Override
    public void addConsentDocument(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        studyConsentApi.addStudyConsent(consent);
    }
    @Override
    public void activateConsentDocument(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        studyConsentApi.setActiveStudyConsent(createdOn);
    }
    @Override
    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent) {
        session.checkSignedIn();

        return userManagementApi.createUser(signUp, roles, consent);
    }
    @Override
    public boolean deleteUser(String email) {
        session.checkSignedIn();

        return userManagementApi.deleteUser(email);
    }
}
