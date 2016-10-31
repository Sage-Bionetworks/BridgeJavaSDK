package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Interceptor;
import okio.Buffer;

public class UserSessionInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInterceptor.class);
    
    private static final String SIGN_OUT_PATH_SEGMENT = "signOut";
    private static final String SIGN_IN_PATH_SEGMENT = "signIn";
    private static final String BRIDGE_SESSION_HEADER = "Bridge-Session";

    private final Map<String,SignIn> sessionTokenMap = new HashMap<>();
    private final Map<SignIn,UserSessionInfo> sessionMap = new HashMap<>();
    
    public synchronized UserSessionInfo getSession(SignIn signIn) {
        UserSessionInfo session = sessionMap.get(signIn);
        if (LOG.isDebugEnabled() && session != null) {
            LOG.info("Retrieving session from cache: " + signIn.getEmail());
        }
        return session;
    }
    
    public synchronized void addSession(SignIn signIn, UserSessionInfo userSessionInfo) {
        if (LOG.isDebugEnabled()) {
            if (sessionMap.containsKey(signIn)) {
                LOG.debug("Updating session in cache: " + signIn.getEmail());
            } else {
                LOG.debug("Adding session to cache: " + signIn.getEmail());    
            }
        }
        sessionMap.put(signIn, userSessionInfo);
        sessionTokenMap.put(userSessionInfo.getSessionToken(), signIn);
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
        SignIn signIn = recoverSignIn(request);
        try {
            Response response = chain.proceed(request);
            if (signIn != null) {
                String bodyString = response.body().string();

                UserSessionInfo userSessionInfo = getUserSessionInfo(bodyString);
                addSession(signIn, userSessionInfo);
                
                return copyResponse(response, bodyString);
            }
            if (isSuccessfulSignOut(request, response)) {
                String sessionToken = request.header(BRIDGE_SESSION_HEADER);
                if (sessionToken != null) {
                    removeSession(sessionToken);
                }
            }
            return response;
        } catch(ConsentRequiredException e) {
            // Only add a session if the exception was thrown during a sign in (and thus we'll have 
            // the sign in information). At other times we just rethrow this exception because it 
            // doesn't invalidate the session. 
            if (signIn != null) {
                addSession(signIn, e.getSession());    
            }
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
            return RestUtils.MAPPER.readValue(string, SignIn.class);
        }
        return null;
    }
    
    private boolean isSuccessfulSignOut(Request request, Response response) {
        String pathSeg = RestUtils.last(request.url().pathSegments());
        return (SIGN_OUT_PATH_SEGMENT.equals(pathSeg) && response.code() == 200);
    }
    
    private UserSessionInfo getUserSessionInfo(String bodyString) throws IOException {
        return RestUtils.MAPPER.readValue(bodyString, UserSessionInfo.class);
    }
}
