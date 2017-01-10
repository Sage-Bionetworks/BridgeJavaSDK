package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.rest.exceptions.BadRequestException;
import org.sagebionetworks.bridge.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.rest.exceptions.ConcurrentModificationException;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.rest.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.rest.exceptions.NotAuthenticatedException;
import org.sagebionetworks.bridge.rest.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.rest.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.rest.exceptions.UnsupportedVersionException;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Convert HTTP bad request and server errors to Java business exceptions.
 *
 */
class ErrorResponseInterceptor implements Interceptor {

    private static final Type ERRORS_MAP_TYPE_TOKEN 
        = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        Response response = chain.proceed(request);
        
        if (response.code() > 399) {
            throwErrorCodeException(response);
        }
        return response;
    }
    
    private void throwErrorCodeException(Response response) {
        String url = response.request().url().toString();
        try {
            JsonElement node = RestUtils.GSON.fromJson(response.body().string(), JsonElement.class);
            throwExceptionOnErrorStatus(url, response.code(), node, null);
        } catch (BridgeSDKException e) {
            throw e; // rethrow known exceptions
        } catch (Throwable t) {
            throwExceptionOnErrorStatus(url, response.code(), null, response.message());
        }
    }

    private void throwExceptionOnErrorStatus(String url, int statusCode, JsonElement node, String message) {
        if (Strings.isNullOrEmpty(message)) {
            if (node != null && node.getAsJsonObject().get("message") != null) {
                message = node.getAsJsonObject().get("message").getAsString();
            } else {
                // Not having a message is actually pretty bad
                message = "There has been an error on the server";
            }
        }

        BridgeSDKException e;
        if (statusCode == 401) {
            e = new NotAuthenticatedException(message, url);
        } else if (statusCode == 403) {
            e = new UnauthorizedException(message, url);
        } else if (statusCode == 404) {
            e = new EntityNotFoundException(message, url);
        } else if (statusCode == 410) {
            e = new UnsupportedVersionException(message, url);
        } else if (statusCode == 412) {
            UserSessionInfo session = null;
            if (node != null) {
                session = RestUtils.GSON.fromJson(node, UserSessionInfo.class);
            }
            e = new ConsentRequiredException("Consent required.", url, session);
        } else if (statusCode == 409 && message.contains("already exists")) {
            e = new EntityAlreadyExistsException(message, url);
        } else if (statusCode == 409 && message.contains("has the wrong version number")) {
            e = new ConcurrentModificationException(message, url);
        } else if (statusCode == 400 && message.contains("A published survey")) {
            e = new PublishedSurveyException(message, url);
        } else if (statusCode == 400) {
            if (node != null && node.getAsJsonObject().get("errors") != null) {
                Map<String, List<String>> errors = RestUtils.GSON.fromJson(node.getAsJsonObject().get("errors"),
                        ERRORS_MAP_TYPE_TOKEN);
                e = new InvalidEntityException(message, errors, url);
            } else {
                e = new BadRequestException(message, url);
            }
        } else {
            e = new BridgeSDKException(message, statusCode, url);
        }
        throw e;
    }
}
