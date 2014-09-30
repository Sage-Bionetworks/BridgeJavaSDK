package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class ClientProvider {

    private UserSession session;
    private AuthenticationApiCaller auth;
    private Config conf;

    private ClientProvider(String configPath) {
        this.auth = AuthenticationApiCaller.valueOf(this);
        this.conf = (configPath == null) ? Config.valueOfDefault() : Config.valueOf(configPath);
    }

    public static ClientProvider valueOf(String configPath) {
        if (configPath == null) {
            throw new IllegalArgumentException("Configuration path must not be null.");
        } else if (!configPath.endsWith(".properties")) {
            throw new IllegalArgumentException(
                    "Argument must end with the suffix \".properties\": " + configPath);
        }
        return new ClientProvider(configPath);
    }

    public static ClientProvider valueOf() {
        return new ClientProvider(null);
    }


    public static boolean isConnectableUrl(String url, int timeout) {
        return Config.isConnectableUrl(url, timeout);
    }

    String getSessionToken() { return (session != null) ? session.getSessionToken() : null; }
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