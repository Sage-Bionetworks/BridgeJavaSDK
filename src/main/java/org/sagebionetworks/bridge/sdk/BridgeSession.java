package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nonnull;

class BridgeSession implements Session {

    private static final String NOT_AUTHENTICATED = "This session has been signed out; create a new session to retrieve a valid client.";

    private String sessionToken;
    private String username;
    private boolean consented;
    private boolean dataSharing;
    private final @Nonnull ClientProvider clientProvider;

    BridgeSession(@Nonnull UserSession session, @Nonnull ClientProvider clientProvider) {
        checkNotNull(session, Bridge.CANNOT_BE_NULL, "UserSession");

        this.username = session.getUsername();
        this.sessionToken = session.getSessionToken();
        this.consented = session.isConsented();
        this.dataSharing = session.isDataSharing();
        this.clientProvider = clientProvider;
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
    public boolean isDataSharing() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return dataSharing;
    }
    
    void setDataSharing(boolean dataSharing) {
        this.dataSharing = dataSharing;
    }

    @Override
    public UserClient getUserClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeUserClient(this, clientProvider);
    }

    @Override
    public ResearcherClient getResearcherClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeResearcherClient(this, clientProvider);
    }

    @Override
    public AdminClient getAdminClient() {
        checkState(isSignedIn(), NOT_AUTHENTICATED);
        return new BridgeAdminClient(this, clientProvider);
    }
    
    @Override
    public boolean isSignedIn() {
        return sessionToken != null;
    }

    @Override
    public synchronized void signOut() {
        if (sessionToken != null) {
            new BaseApiCaller(this, clientProvider).get(clientProvider.getConfig().getAuthSignOutApi());
            sessionToken = null;
        }
    }

    @Override
    public String toString() {
        return "BridgeSession [sessionToken=" + sessionToken + ", username=" + username + ", consented=" + consented
                + ", dataSharing=" + dataSharing + "]";
    }

}
