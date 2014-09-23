package org.sagebionetworks.bridge.sdk;

public class BridgeUserClient {

    private BridgeUserClient() {

    }

    static BridgeUserClient valueOf(UserSession session) {
        return new BridgeUserClient();
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
