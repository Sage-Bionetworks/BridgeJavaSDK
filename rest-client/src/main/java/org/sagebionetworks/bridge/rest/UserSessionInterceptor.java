package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

class UserSessionInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInterceptor.class);

    private static final String SIGN_OUT_PATH_SEGMENT = "signOut";
    private static final String SIGN_IN_PATH_SEGMENT = "signIn";
    private static final String BRIDGE_SESSION_HEADER = "Bridge-Session";

    private final Map<String, SignIn> sessionTokenMap = new HashMap<>();
    private final Map<SignIn, UserSessionInfo> sessionMap = new HashMap<>();

    public synchronized UserSessionInfo getSession(SignIn signIn) {
        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);

        UserSessionInfo session = sessionMap.get(internalSignIn);
        if (LOG.isDebugEnabled() && session != null) {
            LOG.info("Retrieving session from cache: " + internalSignIn.getEmail());
        }
        return session;
    }

    public synchronized void addSession(SignIn signIn, UserSessionInfo userSessionInfo) {
        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);

        if (LOG.isDebugEnabled()) {
            if (sessionMap.containsKey(internalSignIn)) {
                LOG.debug("Updating session in cache: " + internalSignIn.getEmail());
            } else {
                LOG.debug("Adding session to cache: " + internalSignIn.getEmail());
            }
        }
        sessionMap.put(internalSignIn, userSessionInfo);
        sessionTokenMap.put(userSessionInfo.getSessionToken(), internalSignIn);
    }

    public synchronized void removeSession(UserSessionInfo session) {
        if (session == null) {
            return;
        }

        removeSession(session.getSessionToken());
    }

    public synchronized void removeSession(String sessionToken) {
        if (sessionTokenMap.containsKey(sessionToken)) {
            SignIn signIn = sessionTokenMap.get(sessionToken);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removing session from cache: " + signIn.getEmail());
            }
            sessionMap.remove(signIn);
            sessionTokenMap.remove(sessionToken);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        SignIn signIn = null;
        try {
            Response response = chain.proceed(request);

            if (isSuccessfulSignOut(request, response)) {
                String sessionToken = request.header(BRIDGE_SESSION_HEADER);
                if (sessionToken != null) {
                    removeSession(sessionToken);
                }
                return response;
            }

            // TODO: consider white-listing URLs instead of always attempting session retrieval
            String bodyString = response.body().string();
            UserSessionInfo userSessionInfo = getUserSessionInfo(bodyString);
            if (userSessionInfo != null) {
                signIn = recoverSignIn(request);
                if (signIn == null) {
                    // if this wasn't a sign in, check if we previously captured the sign in
                    signIn = sessionTokenMap.get(userSessionInfo.getSessionToken());
                }
                if (signIn != null) {
                    addSession(signIn, userSessionInfo);
                }
            }

            // response body was already read, create copy instance before passing up the chain
            return copyResponse(response, bodyString);
        } catch (ConsentRequiredException e) {
            signIn = recoverSignIn(request);
            if (signIn == null) {
                // if this wasn't a sign in, check if we previously captured the sign in
                signIn = sessionTokenMap.get(e.getSession().getSessionToken());
            }
            if (signIn != null) {
                addSession(signIn, e.getSession());
            }
            // We just rethrow this exception because it doesn't invalidate the session.
            throw e;
        }
    }

    protected Response copyResponse(Response response, String bodyString) throws IOException {
        MediaType contentType = response.body().contentType();
        ResponseBody body = ResponseBody.create(contentType, bodyString.getBytes());
        Response newResponse = response.newBuilder().body(body).build();
        return newResponse;
    }

    protected Buffer createBuffer() {
        return new Buffer();
    }

    private SignIn recoverSignIn(Request request) throws IOException {
        String pathSeg = RestUtils.last(request.url().pathSegments());
        if (SIGN_IN_PATH_SEGMENT.equals(pathSeg)) {
            Request newRequest = request.newBuilder().build();
            Buffer buffer = createBuffer();
            newRequest.body().writeTo(buffer);
            String string = buffer.readUtf8();

            SignIn rawSignIn = RestUtils.GSON.fromJson(string, SignIn.class);
            return RestUtils.makeInternalCopy(rawSignIn);
        }
        return null;
    }

    private boolean isSuccessfulSignOut(Request request, Response response) {
        String pathSeg = RestUtils.last(request.url().pathSegments());
        return (SIGN_OUT_PATH_SEGMENT.equals(pathSeg) && response.code() == 200);
    }

    private UserSessionInfo getUserSessionInfo(String bodyString) {
        try {
            return RestUtils.GSON.fromJson(bodyString, UserSessionInfo.class);
        } catch (JsonSyntaxException e) {
            LOG.debug("Failed to deserialize UserSessionInfo from response", e);
            // this likely means we didn't have a UserSessionInfo return type
            return null;
        }
    }
}
