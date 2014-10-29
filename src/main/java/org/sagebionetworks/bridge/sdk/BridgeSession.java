package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotNull;
import static org.sagebionetworks.bridge.sdk.Preconditions.checkState;

class BridgeSession implements Session {

    private static final String NOT_AUTHENTICATED = "This session has been signed out; create a new session to retrieve a valid client.";
    private UserSession session;
    private AuthenticationApiCaller authApi;
    
    private BridgeSession(UserSession session) {
        checkNotNull(session, "UserSession is null");
        
        this.session = session;
        this.authApi = AuthenticationApiCaller.valueOf(this);
    }

    static BridgeSession valueOf(UserSession session) {
        return new BridgeSession(session);
    }
    
    public void checkSignedIn() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
    }
    
    @Override
    public String getUsername() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return session.getUsername();
    }

    @Override
    public String getSessionToken() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return session.getSessionToken();
    }

    @Override
    public boolean isConsented() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return session.isConsented();
    }

    @Override
    public boolean isDataSharing() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return session.isDataSharing();
    }
    
    @Override
    public UserClient getUserClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return BridgeUserClient.valueOf(this);
    }

    @Override
    public ResearcherClient getResearcherClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return BridgeResearcherClient.valueOf(this);
    }

    @Override
    public AdminClient getAdminClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return BridgeAdminClient.valueOf(this);
    }
    
    @Override
    public boolean isSignedIn() {
        return session != null;
    }

    @Override
    public synchronized void signOut() {
        if (session != null) {
            authApi.signOut();
            session = null;
        }
    }

}
