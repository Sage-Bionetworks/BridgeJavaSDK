package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Inspects requests and responses to keep session and sign in credentials updated.
 */
class UserSessionInterceptor implements Interceptor {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInterceptor.class);

    private static class ResponseMatcher {
        private final int[] statuses;
        private final String method;
        private final String pathSegment;
        ResponseMatcher(String method, String pathSegment, int... statuses) {
            this.statuses = statuses;
            this.method = method.toLowerCase();
            this.pathSegment = pathSegment.toLowerCase();
        }
        boolean responseMatches(Response response) {
            Request request = response.request();
            return statusCorrect(response.code()) &&
                   request.method().toLowerCase().equals(method) && 
                   request.url().toString().toLowerCase().contains(pathSegment); 
        }
        boolean statusCorrect(int responseStatus) {
            for (int status : statuses) {
                if (responseStatus == status) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static final List<ResponseMatcher> SIGNOUT_PATHS = ImmutableList.of(
            new ResponseMatcher("POST", "/signOut", 200));
    public static final List<ResponseMatcher> SESSION_PATHS = ImmutableList.of(
            new ResponseMatcher("POST", "/auth/", 200, 412),
            new ResponseMatcher("POST", "/v3/participants/self", 200));

    private UserSessionInfoProvider userSessionInfoProvider;

    public UserSessionInterceptor(UserSessionInfoProvider userSessionInfoProvider) {
        this.userSessionInfoProvider = userSessionInfoProvider;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            Response response = chain.proceed(request);
            
            if (testResponse(response, SIGNOUT_PATHS)) {
                userSessionInfoProvider.setSession(null);
            } else if (testResponse(response, SESSION_PATHS)) {
                String bodyString = response.body().string();
                UserSessionInfo tempSession = getUserSessionInfo(bodyString);
                if (tempSession != null) {
                    userSessionInfoProvider.setSession(tempSession);
                }
                return copyResponse(response, bodyString);
            }
            if (response.code() > 399) {
                LOG.warn("Received an error code when an exception was " +
                        "expected, UserSessionInfo may not be correctly updated");
            }
            return response;
        } catch(ConsentRequiredException e) {
            userSessionInfoProvider.setSession(e.getSession());
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
    
    private boolean testResponse(Response response, List<ResponseMatcher> matchers) {
        for (ResponseMatcher matcher : matchers) {
            if (matcher.responseMatches(response)) {
                return true;
            }
        }
        return false;
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
