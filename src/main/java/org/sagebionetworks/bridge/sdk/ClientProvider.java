package org.sagebionetworks.bridge.sdk;

public class ClientProvider {

    private BridgeUserClient client;
    private UserSession authenticatedSession;

    private ClientProvider(Version version) {
        this.client = null;
        this.authenticatedSession = null;

        AuthenticationApiCaller.setVersion(version);
    }

    public static ClientProvider valueOf(Version version) {
        if (version == null) {
            throw new IllegalArgumentException("Version must not be null.");
        }
        return new ClientProvider(version);
    }

    public void authenticate(SignInCredentials signIn) {
        if (signIn == null) {
            throw new IllegalArgumentException("SignInCredentials object must not be null.");
        }
        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf();
        authenticatedSession = auth.signIn(signIn.getUsername(), signIn.getPassword());
    }

    public BridgeUserClient getClient() {
        if (authenticatedSession == null) {
            throw new IllegalStateException("A User needs to be signed in to call this method.");
        }
        client = BridgeUserClient.valueOf(authenticatedSession);
        return client;
    }

}
