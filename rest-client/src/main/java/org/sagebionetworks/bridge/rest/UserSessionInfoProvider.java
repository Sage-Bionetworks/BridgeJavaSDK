package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.ReauthenticateRequest;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

/**
 * Retrieves session information for a Bridge user. It either gets it from the user session interceptor, or if 
 * a session doesn't exist (hasn't been captured), it authenticates. The authentication causes the interceptor to 
 * capture the session, then you can return it.
 */
public class UserSessionInfoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);

    private final AuthenticationApi authenticationApi;
    private final SignIn signIn;
    private UserSessionInfo session;

    UserSessionInfoProvider(AuthenticationApi authenticationApi, SignIn signIn) {
        checkNotNull(authenticationApi);
        checkNotNull(signIn);
        
        this.authenticationApi = authenticationApi;
        this.signIn = signIn;
    }
    
    public synchronized UserSessionInfo getSession() {
        return session;
    }
    
    /**
     * Retrieves a session. If a session hasn't been captured by an interceptor, this provider will attempt 
     * to retrieve it by signing in.
     *
     * @return session info for the user
     * @throws IOException problem while signing in
     */
    public synchronized UserSessionInfo retrieveSession() throws IOException {
        if (session != null) {
            return session;
        }
        // There's no session, so our only option is to sign in again.
        if (LOG.isDebugEnabled()) {
            LOG.debug(debugString("null")+ " signing in");
        }
        signIn();
        return session;
    }
    
    synchronized void setSession(UserSessionInfo session) {
        if (LOG.isDebugEnabled()) {
            String token = (session==null) ? "null" : session.getSessionToken();
            LOG.debug(debugString(token));
        }
        this.session = session;
    }
    
    synchronized void reauthenticate() throws IOException {
        if (session == null || session.getReauthToken() == null) {
            signIn();
        } else {
            ReauthenticateRequest request = new ReauthenticateRequest().email(session.getEmail())
                    .reauthToken(session.getReauthToken()).study(signIn.getStudy());
            try {
                UserSessionInfo newSession = authenticationApi.reauthenticate(request).execute().body();
                setSession(newSession);
            } catch(ConsentRequiredException e) {
                setSession(e.getSession());
            } catch(Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reauth failed, signing in again");
                }
                signIn();
            }
        }
    }
    
    private void signIn() throws IOException {
        try {
            UserSessionInfo newSession = authenticationApi.signIn(signIn).execute().body();
            setSession(newSession); 
        } catch(ConsentRequiredException e) {
            setSession(e.getSession());
        }
    }
    
    private String debugString(String token) {
        return "userSessionInfoProvider["+this.hashCode()+"].session="+token;
    }
}
