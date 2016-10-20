package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.sagebionetworks.bridge.sdk.rest.ApiClientProvider;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

import com.google.common.collect.ImmutableMap;

public class ClientManager {

    private Map<org.sagebionetworks.bridge.sdk.rest.model.Environment,String> HOSTS = 
            new ImmutableMap.Builder<org.sagebionetworks.bridge.sdk.rest.model.Environment,String>()
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.LOCAL,"http://localhost:9000")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.DEVELOP,"https://webservices-develop.sagebridge.org")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.STAGING,"https://webservices-staging.sagebridge.org")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.PRODUCTION,"https://webservices.sagebridge.org")
            .build();
    
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
            apiClientProvider = new ApiClientProvider(HOSTS.get(config.getEnvironment2()), clientInfo.toString());
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
            apiClientProvider = new ApiClientProvider(HOSTS.get(config.getEnvironment2()), clientInfo.toString());
        }
        return apiClientProvider.getClient(service, signIn);
    }
    
}
