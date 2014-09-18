package org.sagebionetworks.bridge.sdk.user;


public final class User {
    
    private final UserSession session;
    // TODO private final UserProfile profile;
    // TODO private final UserData data;

    private User(UserSession session) {
        this.session = session;
    }

    public UserSession getSession() { return this.session; }

    // TODO: add profile and data, as we want to update the user all at once.
    public void set(UserSession session) { new User(session); }
}
