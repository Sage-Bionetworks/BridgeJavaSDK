package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

final class BridgeAdminClient implements AdminClient {

    private final Session session;
    private final UserManagementApiCaller userManagementApi;

    private BridgeAdminClient(Session session) {
        this.session = session;
        this.userManagementApi = UserManagementApiCaller.valueOf(session);
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
}
