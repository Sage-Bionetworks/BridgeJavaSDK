package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts authentication error responses and re-acquires a session. NOTE: This handler is not currently registered
 * by the AuthenticatedClientProvider to handle authentication, because no integration test indicates that it is needed. When we
 * detect a session is missing, we reauthenticate using the UserSessionInfoProvider. It may be that this is needed when
 * a session times out on the server, if so, no test currently simulates this behavior.
 */
class AuthenticationHandler implements Interceptor, Authenticator {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationHandler.class);

    private static final int MAX_TRIES = 1;
    private static final String SIGN_OUT_PATH = "/signOut";
    private static final String AUTH_PATH = "/auth/";
    
    private final UserSessionInfoProvider userSessionInfoProvider;
    private int tryCount;

    public AuthenticationHandler(UserSessionInfoProvider userSessionInfoProvider) {
        checkNotNull(userSessionInfoProvider);
        this.userSessionInfoProvider = userSessionInfoProvider;
    }
    
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (tryCount >= MAX_TRIES || !requiresAuth(response.request(), false)) {
            tryCount = 0;
            return null;
        }
        
        LOG.debug("Received 401, automatically attempting authentication, tryCount: " + tryCount);

        // We received a 401 from the server... attempt to reauthenticate.
        tryCount++;
        userSessionInfoProvider.reauthenticate();
        // We should now be able to proceed with session headers.
        return addBridgeHeaders(response.request());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        // Basically, everything in the auth controller can skip a session token, except for sign out
        if (requiresAuth(request, true)) {
            request = addBridgeHeaders(request);
        }
        return chain.proceed(request);
    }
    
    private boolean requiresAuth(Request request, boolean includeSignOut) {
        String url = request.url().toString();
        return (!url.contains(AUTH_PATH) || (includeSignOut && url.endsWith(SIGN_OUT_PATH)));
    }

    private Request addBridgeHeaders(Request request) throws IOException {
        String sessionToken = getSessionToken();
        if (sessionToken == null) {
            return request;
        }
        return request.newBuilder().header(HeaderInterceptor.BRIDGE_SESSION, sessionToken).build();
    }

    // allow tests to inspect current session token
    private String getSessionToken() throws IOException {
        UserSessionInfo session = this.userSessionInfoProvider.retrieveSession();
        return (session == null) ? null : session.getSessionToken();
    }
}