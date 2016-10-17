package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.rest.ApiClientProvider;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

import com.google.common.base.Preconditions;

public class ClientManager {

    private Config config = new Config();

    private ClientInfo clientInfo = new ClientInfo.Builder().build();
    
    /**
     * This is the default, to look in the bridge-sdk.properties file, but it can 
     * be overwritten when creating the manager.
     */
    private SignIn signIn = config.getAccountSignIn();

    protected ApiClientProvider apiClientProvider;
    
    public final ClientManager clientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
        return this;
    }

    public final ClientManager signIn(SignIn signIn) {
        this.signIn = signIn;
        return this;
    }
    
    /**
     * Creates an unauthenticated client.
     *
     * @param service
     *         Class representing the service
     * @return service client
     */
    public <T> T getUnauthenticatedClient(Class<T> service) {
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
    public <T> T getAuthenticatedClient(Class<T> service) {
        Preconditions.checkNotNull(signIn, "Sign in credentials are required to create an authenticated client.");
        if (apiClientProvider == null) {
            apiClientProvider = new ApiClientProvider(config.getEnvironment().getUrl(), clientInfo.toString());
        }
        return apiClientProvider.getClient(service, signIn);
    }
    
}
