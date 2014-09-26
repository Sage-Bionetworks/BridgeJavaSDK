package org.sagebionetworks.bridge.sdk;

public class ClientProvider {

    private BridgeUserClient client;
    private UserSession authenticatedSession;
    private Config config;

    private ClientProvider(Version version, String configPath) {
        this.client = null;
        this.authenticatedSession = null;
        this.config = (configPath == null ? Config.valueOfDefault() : Config.valueOf(configPath));

        HostName.setUrl(config.getUrl());
        AuthenticationApiCaller.setVersion(version);
    }

    public static ClientProvider valueOf(Version version) {
        if (version == null) {
            throw new IllegalArgumentException("Version must not be null.");
        }
        return new ClientProvider(version, null);
    }

    public static ClientProvider valueOf(Version version, String configPath) {
        if (version == null) {
            throw new IllegalArgumentException("Version must not be null.");
        } else if (configPath == null || configPath.isEmpty() || !configPath.endsWith(".properties")) {
            throw new IllegalArgumentException("ConfigPath must be non-null, non-empty, and end with \".properties\": "
                    + "\"" + configPath + "\"");
        }
        return new ClientProvider(version, configPath);
    }

    public boolean isAuthenticated() {
        return authenticatedSession != null;
    }

    public void authenticate(SignInCredentials signIn) {
        if (signIn == null) {
            throw new IllegalArgumentException("SignInCredentials object must not be null.");
        }
        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf();
        authenticatedSession = auth.signIn(signIn.getUsername(), signIn.getPassword());
    }

    public void signOut() {
        if (authenticatedSession == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf();
        auth.signOut(authenticatedSession);
        authenticatedSession = null;
    }

    public BridgeUserClient getClient() {
        if (authenticatedSession == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        client = BridgeUserClient.valueOf(authenticatedSession);
        return client;
    }

}
