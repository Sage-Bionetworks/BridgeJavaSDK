package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.UserProfile;

public class BridgeUserClient extends BaseApiCaller {

    private BridgeUserClient(ClientProvider provider) {
        super(provider);
    }

    static BridgeUserClient valueOf(ClientProvider provider) {
        return new BridgeUserClient(provider);
    }

    String getRole() {
        return null;

    }

    UserProfile getProfile() {
        return null;
    }

    UserSession getSession() {
        return null;
    }

    String setRole(String role) {
        return null;
    }

    void saveProfile(UserProfile profile) {}

    void saveSession(UserSession session) {}

}
