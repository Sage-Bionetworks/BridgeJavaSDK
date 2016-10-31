package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

/**
 * Retrieves session information for a Bridge user.
 */
class UserSessionInfoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);

    private final AuthenticationApi authenticationApi;
    private UserSessionInterceptor sessionInterceptor;
    
    public UserSessionInfoProvider(Retrofit retrofit, UserSessionInterceptor sessionInterceptor) {
        this.authenticationApi = retrofit.create(AuthenticationApi.class);
        this.sessionInterceptor = sessionInterceptor;
    }

    /**
     * Retrieves a session from a service.
     *
     * @param signIn
     *         sign in credentials for a user
     * @return session info for the user, or null if the return value was not a 200
     * @throws IOException
     */
    public UserSessionInfo retrieveSession(SignIn signIn) throws IOException {
        UserSessionInfo session = sessionInterceptor.getSession(signIn);
        if (session != null) {
            return session;
        }
        LOG.debug("No session, call intercepted for authentication attempt: " + signIn.getEmail());
        authenticationApi.signIn(signIn).execute();
        
        // Calling sign in will cause the session to be saved by the session interceptor. No further 
        // work is required here.
        return sessionInterceptor.getSession(signIn);
    }
}
