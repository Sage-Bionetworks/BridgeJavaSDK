package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.sagebionetworks.bridge.sdk.rest.ApiClientProvider;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

import com.google.common.collect.ImmutableMap;

public final class ClientManager {
    
    private Map<org.sagebionetworks.bridge.sdk.rest.model.Environment,String> HOSTS = 
            new ImmutableMap.Builder<org.sagebionetworks.bridge.sdk.rest.model.Environment,String>()
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.LOCAL,"http://localhost:9000")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.DEVELOP,"https://webservices-develop.sagebridge.org")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.STAGING,"https://webservices-staging.sagebridge.org")
            .put(org.sagebionetworks.bridge.sdk.rest.model.Environment.PRODUCTION,"https://webservices.sagebridge.org")
            .build();
    
    private final Config config;
    private final ClientInfo clientInfo;
    private final transient SignIn signIn;
    
    private ClientManager(Config config, ClientInfo clientInfo, SignIn signIn) {
        this.config = config;
        this.clientInfo = clientInfo;
        this.signIn = signIn;
        this.apiClientProvider = new ApiClientProvider(HOSTS.get(config.getEnvironment2()), clientInfo.toString());
    }
    
    private ApiClientProvider apiClientProvider;
    
    public final Config getConfig() {
        return config;
    }
    
    public final ClientInfo getClientInfo() {
        return clientInfo;
    }

    /**
     * @param service
     *         Class representing the service
     * @return service client
     */
    public final <T> T getClient(Class<T> service) {
        return apiClientProvider.getClient(service, signIn);
    }
    
    public final static class Builder {
        private Config config;
        private ClientInfo clientInfo;
        private SignIn signIn;
        
        public Builder withConfig(Config config) {
            this.config = config;
            return this;
        }
        public Builder withClientInfo(ClientInfo clientInfo) {
            this.clientInfo = clientInfo;
            return this;
        }
        public Builder withSignIn(SignIn signIn) {
            this.signIn = signIn;
            return this;
        }
        public ClientManager build() {
            checkNotNull(signIn, "Sign in must be supplied to ClientManager builder.");
            checkNotNull(signIn.getStudy(), "Sign in must have a study identifier.");
            checkNotNull(signIn.getEmail(), "Sign in must specify an email address.");
            checkNotNull(signIn.getPassword(), "Sign in must specify a password.");
            if (this.config == null) {
                this.config = new Config();
            }
            if (this.clientInfo == null) {
                this.clientInfo = new ClientInfo.Builder().build();
            }
            return new ClientManager(config, clientInfo, signIn);
        }
    }
    
}
