package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class ClientProvider {

    private UserSession session;
    private String host;
    private AuthenticationApiCaller auth;

    private ClientProvider(String host) {
        this.host = host;
        this.auth = AuthenticationApiCaller.valueOf(this);
    }

    public static ClientProvider valueOf(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Host must not be null.");
        }
        return new ClientProvider(host);
    }

    public static ClientProvider valueOf() {
        return new ClientProvider(HostName.getProd());
    }

    String getSessionToken() {
        return (session != null) ? session.getSessionToken() : null;
    }

    String getHost() {
        return host;
    }

    public boolean isSignedIn() {
        return session != null;
    }

    public void signIn(SignInCredentials signIn) {
        if (signIn == null) {
            throw new IllegalArgumentException("SignInCredentials object must not be null.");
        }
        session = auth.signIn(signIn.getUsername(), signIn.getPassword());
    }

    public void signOut() {
        if (session != null) {
            auth.signOut(session);
            session = null;
        }
    }

    public BridgeUserClient getClient() {
        if (session == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        return BridgeUserClient.valueOf(this);
    }

    public BridgeResearcherClient getResearcherClient() {
        if (session == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        return BridgeResearcherClient.valueOf(this);
    }
}