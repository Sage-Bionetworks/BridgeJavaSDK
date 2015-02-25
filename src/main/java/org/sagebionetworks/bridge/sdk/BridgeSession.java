package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class BridgeSession implements Session {

    private static final String NOT_AUTHENTICATED = "This session has been signed out; create a new session to retrieve a valid client.";

    private ScopeOfSharing scopeOfSharing;
    private String sessionToken;
    private String username;
    private boolean consented;
    
    BridgeSession(UserSession session) {
        checkNotNull(session, Bridge.CANNOT_BE_NULL, "UserSession");
        
        this.username = session.getUsername();
        this.sessionToken = session.getSessionToken();
        this.consented = session.isConsented();
        this.scopeOfSharing = session.getScopeOfSharing();
    }

    /**
     * Check that the client is currently authenticated, throwing an exception 
     * if it is not.
     * @throws IllegalStateException
     */
    public void checkSignedIn() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
    }
    
    @Override
    public String getUsername() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return username;
    }
    
    void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getSessionToken() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return sessionToken;
    }
    
    @Override
    public boolean isConsented() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return consented;
    }

    void setConsented(boolean consented) {
        this.consented = consented;
    }
    
    @Override
    public ScopeOfSharing getScopeOfSharing() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return scopeOfSharing;
    }
    
    void setScopeOfSharing(ScopeOfSharing scopeOfSharing) {
        this.scopeOfSharing = scopeOfSharing;
    }
    
    @Override
    public UserClient getUserClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeUserClient(this);
    }

    @Override
    public ResearcherClient getResearcherClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeResearcherClient(this);
    }

    @Override
    public AdminClient getAdminClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeAdminClient(this);
    }
    
    @Override
    public boolean isSignedIn() {
        return sessionToken != null;
    }

    @Override
    public synchronized void signOut() {
        if (sessionToken != null) {
            new BaseApiCaller(this).get(ClientProvider.getConfig().getAuthSignOutApi());
            sessionToken = null;
        }
    }

    @Override
    public String toString() {
        return String.format("BridgeSession [sessionToken=%s, username=%s, consented=%s, scopeOfSharing=%s]", 
                sessionToken, username, consented, scopeOfSharing.name().toLowerCase());
    }

}
