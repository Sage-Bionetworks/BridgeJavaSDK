package org.sagebionetworks.bridge.rest;

import java.io.IOException;

import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import retrofit2.Retrofit;

/**
 * Retrieves session information for a Bridge user.
 */
class UserSessionInfoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);

    private final AuthenticationApi authenticationApi;
    private final UserSessionInterceptor sessionInterceptor;
    
    public UserSessionInfoProvider(Retrofit retrofit, UserSessionInterceptor sessionInterceptor) {
        this.authenticationApi = retrofit.create(AuthenticationApi.class);
        this.sessionInterceptor = sessionInterceptor;
    }

    // intended for use by AuthenticationHandler
    void removeSession(UserSessionInfo session) {
        if (session == null) {
            return;
        }
        LOG.debug("Removing session: " + session.getEmail());
        this.sessionInterceptor.removeSession(session);
    }

    /**
     * Retrieves a session. Tries to retrieve from a cache before calling a service.
     *
     * @param signIn
     *         sign in credentials for a user
     * @return session info for the user, or null if the return value was not a 200
     * @throws IOException
     */
    public UserSessionInfo retrieveSession(SignIn signIn) throws IOException {
        UserSessionInfo session = retrieveCachedSession(SignIn signIn)

        LOG.debug("No session, call intercepted for authentication attempt: " + signIn.getEmail());
        authenticationApi.signIn(signIn).execute();
        
        // Calling sign in will cause the session to be saved by the session interceptor. No further 
        // work is required here.
        return sessionInterceptor.getSession(signIn);
    }

    /**
     * Retrieves a session without making a network call.
     *
     * @param signIn
     *         sign in credentials for a user
     * @return session info for the user
     */
    public UserSessionInfo retrieveCachedSession(SignIn signIn) throws
            IOException {
        if (signIn == null) {
            return null;
        }

        return sessionInterceptor.getSession(signIn);
    }
}
