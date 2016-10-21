package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.rest.ApiClientProvider;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

public class ClientManager {

    private final Config config = new Config();

    private ClientInfo clientInfo = new ClientInfo.Builder().build();
    
    private ApiClientProvider apiClientProvider;
    
    public final Config getConfig() {
        return config;
    }
    
    public final ClientManager clientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
        return this;
    }

    /**
     * Creates an unauthenticated client.
     *
     * @param service
     *         Class representing the service
     * @return service client
     */
    public final <T> T getUnauthenticatedClient(Class<T> service) {
        if (apiClientProvider == null) {
            apiClientProvider = new ApiClientProvider(config.getEnvironment().getUrl(), clientInfo.toString());
        }
        return apiClientProvider.getClient(service);
    }

    /**
     * @param service
     *         Class representing the service
     * @param signIn
     *         credentials for the user, or null for an unauthenticated client
     * @return service client that is authenticated with the user's credentials
     */
    public final <T> T getAuthenticatedClient(Class<T> service, SignIn signIn) {
        checkNotNull(signIn, "Sign in credentials are required to create an authenticated client.");
        if (apiClientProvider == null) {
            apiClientProvider = new ApiClientProvider(config.getEnvironment().getUrl(), clientInfo.toString());
        }
        return apiClientProvider.getClient(service, signIn);
    }
    
}
