package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.Phone;
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
    private final String studyId;
    private final String email;
    private final Phone phone;
    private final String password;
    private UserSessionInfo session;

    UserSessionInfoProvider(AuthenticationApi authenticationApi, String studyId, String email, Phone phone, String password,
            UserSessionInfo session) {
        checkNotNull(authenticationApi);
        checkNotNull(studyId);
        checkState(email != null || phone != null, "requires either email or phone");

        this.authenticationApi = authenticationApi;
        this.studyId = studyId;
        this.email = email;
        this.phone = phone;
        this.session = session;
        this.password = password;
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
    
    synchronized void setSession(UserSessionInfo newSession) {
        if (LOG.isDebugEnabled()) {
            String token = (newSession==null) ? "null" : newSession.getSessionToken();
            LOG.debug(debugString(token));
        }

        mergeReauthToken(this.session, newSession);
        this.session = newSession;
    }

    /**
     * The server is only guaranteed to send the reauth token on sign in, it is not sent in all UserSessionInfo
     * responses for security reasons. If the previous  session contains a token and the new session does not, copy
     * over the token from the previous session
     *
     * @param previousSession
     *         older session
     * @param session
     *         new session, with reauthToken copied over from previousSession if the new session did not have
     *         a reauth token
     */
    public static void mergeReauthToken(final UserSessionInfo previousSession, final UserSessionInfo session) {
        if (session != null && session.getReauthToken() == null) {
            if (previousSession != null) {
                session.setReauthToken(previousSession.getReauthToken());
            }
        }
    }
    
    synchronized void reauthenticate() throws IOException {
        if (session == null || session.getReauthToken() == null) {
            signIn();
        } else {
            SignIn request = new SignIn().email(email).phone(phone)
                    .reauthToken(session.getReauthToken()).study(studyId);
            try {
                UserSessionInfo newSession = authenticationApi.reauthenticate(request).execute().body();
                setSession(newSession);
            } catch(ConsentRequiredException e) {
                // successful authentication
                setSession(e.getSession());
            } catch(BridgeSDKException reauthException) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reauth failed, signing in again", reauthException);
                }

                boolean signInAttempted;
                try {
                     signInAttempted = signIn();
                } catch (BridgeSDKException signInException) {
                    try {
                        signInException.initCause(reauthException);
                    } catch (IllegalStateException t) {
                        // should not happen, since cause should not have been set before
                        LOG.debug("Unable to set cause on: ", signInException);
                    }
                    throw signInException;
                }

                // if we never try to sign in, propagate exception from reauth
                if (!signInAttempted) {
                    throw reauthException;
                }
            }
        }
    }
    
    private boolean signIn() throws IOException {
        if (Strings.isNullOrEmpty(password)) {
            LOG.warn("Could not signIn, no password provided");
            return false;
        }
        try {
            SignIn signIn = new SignIn().study(studyId).email(email).phone(phone).password(password);
            UserSessionInfo newSession = authenticationApi.signInV4(signIn).execute().body();
            setSession(newSession); 
        } catch(ConsentRequiredException e) {
            // successful authentication
            setSession(e.getSession());
        }
        return true;
    }
    
    private String debugString(String token) {
        return "userSessionInfoProvider["+this.hashCode()+"].session="+token;
    }
}
