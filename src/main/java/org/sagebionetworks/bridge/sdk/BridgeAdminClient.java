package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

final class BridgeAdminClient implements AdminClient {

    private final Session session;
    private final UserManagementApiCaller userManagementApi;
    private final StudyApiCaller studyApi;

    private BridgeAdminClient(Session session) {
        this.session = session;
        this.userManagementApi = UserManagementApiCaller.valueOf(session);
        this.studyApi = StudyApiCaller.valueOf(session);
    }

    static BridgeAdminClient valueOf(Session session) {
        return new BridgeAdminClient(session);
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
    @Override
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return studyApi.getStudyForAdmin(identifier);
    }
    @Override
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return studyApi.getAllStudies();
    }
    @Override
    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        return studyApi.createStudy(study);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        return studyApi.updateStudyForAdmin(study);
    }
    @Override
    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        studyApi.deleteStudy(identifier);
    }
}
