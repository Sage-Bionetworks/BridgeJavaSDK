package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;

import com.google.common.collect.ImmutableMap;

public final class ClientManager {
    
    private Map<Environment,String> HOSTS = new ImmutableMap.Builder<Environment,String>()
            .put(Environment.LOCAL,"http://localhost:9000")
            .put(Environment.DEVELOP,"https://webservices-develop.sagebridge.org")
            .put(Environment.STAGING,"https://webservices-staging.sagebridge.org")
            .put(Environment.PRODUCTION,"https://webservices.sagebridge.org")
            .build();
    
    private final Config config;
    private final ClientInfo clientInfo;
    private final transient SignIn signIn;
    private final ApiClientProvider apiClientProvider;
    
    private ClientManager(Config config, ClientInfo clientInfo, SignIn signIn) {
        checkNotNull(HOSTS.get(config.getEnvironment()));
        
        this.config = config;
        this.clientInfo = clientInfo;
        this.signIn = signIn;
        String userAgent = RestUtils.getUserAgent(clientInfo);
        this.apiClientProvider = new ApiClientProvider(HOSTS.get(config.getEnvironment()), userAgent);
    }
    
    public final Config getConfig() {
        return config;
    }
    
    public final ClientInfo getClientInfo() {
        return clientInfo;
    }
    
    public final String getHostUrl() {
        return HOSTS.get(config.getEnvironment());
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
        private ClientInfo getDefaultClientInfo() {
            ClientInfo info = new ClientInfo();
            info.setOsName(System.getProperty("os.name"));
            info.setOsVersion(System.getProperty("os.version"));
            info.setDeviceName(System.getProperty("os.arch"));
            info.setSdkName("BridgeJavaSDK");
            info.setSdkVersion(Integer.parseInt(config.getSdkVersion()));
            return info;
        }
        public ClientManager build() {
            if (this.signIn == null && this.config != null) {
                this.signIn = this.config.getAccountSignIn();
            }
            checkNotNull(signIn, "Sign in must be supplied to ClientManager builder.");
            checkNotNull(signIn.getStudy(), "Sign in must have a study identifier.");
            checkNotNull(signIn.getEmail(), "Sign in must specify an email address.");
            checkNotNull(signIn.getPassword(), "Sign in must specify a password.");
            if (this.config == null) {
                this.config = new Config();
            }
            ClientInfo info = getDefaultClientInfo();
            if (this.clientInfo != null) {
                if (clientInfo.getAppName() != null) {
                    info.setAppName(clientInfo.getAppName());
                }
                if (clientInfo.getAppVersion() != null) {
                    info.setAppVersion(clientInfo.getAppVersion());
                }
                if (clientInfo.getDeviceName() != null) {
                    info.setDeviceName(clientInfo.getDeviceName());
                }
                if (clientInfo.getOsName() != null) {
                    info.setOsName(clientInfo.getOsName());
                }
                if (clientInfo.getOsVersion() != null) {
                    info.setOsVersion(clientInfo.getOsVersion());
                }
            }
            return new ClientManager(config, info, signIn);
        }
    }
    
}
