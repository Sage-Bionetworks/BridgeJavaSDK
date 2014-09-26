package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class ClientProvider {

    private UserSession session;
    private String host;
    private AuthenticationApiCaller auth;
    private Config conf;

    private ClientProvider(String host) {
        this.host = host;
        this.auth = AuthenticationApiCaller.valueOf(this);
        this.conf = Config.valueOfDefault();

    }

    public static ClientProvider valueOf(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Host must not be null.");
        } else if (!HostName.isValidUrl(host)) {
            throw new IllegalArgumentException("Host name is not valid: " + host);
        }
        // Ignore this section for now - our system is not pingable.
//        else if (!HostName.isConnectableUrl(host, 1000)) {
//            throw new IllegalArgumentException("Cannot connect to host: " + host);
//        }
        else if (!host.endsWith("/")) {
            host = host + "/";
        }
        return new ClientProvider(host);
    }

    public static ClientProvider valueOf() {
        return new ClientProvider(HostName.getProd());
    }

    String getSessionToken() { return (session != null) ? session.getSessionToken() : null; }
    String getHost() { return host; }
    Config getConfig() { return conf; }

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
        if (session == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        auth.signOut(session);
        session = null;
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