package org.sagebionetworks.bridge.rest;

import java.io.IOException;

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

    public static final String SIGN_OUT_PATH = "/signOut";
    public static final String AUTH_PATH = "/auth/";

    private UserSessionInfoProvider userSessionInfoProvider;

    public UserSessionInterceptor(UserSessionInfoProvider userSessionInfoProvider) {
        this.userSessionInfoProvider = userSessionInfoProvider;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            Response response = chain.proceed(request);
            
            String url = request.url().toString();
            // Don't bother trying to parse a session if it doesn't match at least these criteria
            boolean isOkAuthPath = (url.contains(AUTH_PATH) && response.code() == 200);
            
            if (url.endsWith(SIGN_OUT_PATH)) {
                userSessionInfoProvider.setSession(null);
            } else if (isOkAuthPath) {
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
