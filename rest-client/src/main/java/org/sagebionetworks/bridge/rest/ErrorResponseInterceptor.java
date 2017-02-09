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
import org.sagebionetworks.bridge.rest.exceptions.ConstraintViolationException;
import org.sagebionetworks.bridge.rest.exceptions.EndpointNotFoundException;
import org.sagebionetworks.bridge.rest.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.rest.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.rest.exceptions.NotAuthenticatedException;
import org.sagebionetworks.bridge.rest.exceptions.NotImplementedException;
import org.sagebionetworks.bridge.rest.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.rest.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.rest.exceptions.UnsupportedVersionException;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
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
        // This does not return an exception message, it returns session object.
        if (statusCode == 412) {
            UserSessionInfo session = null;
            if (node != null) {
                session = RestUtils.GSON.fromJson(node, UserSessionInfo.class);
            }
            throw new ConsentRequiredException("Consent required.", url, session);
        }
        String type = (node != null) ? node.getAsJsonObject().get("type").getAsString() : "Unknown";
        switch(type) {
            case "NotAuthenticatedException":
                throw new NotAuthenticatedException(message, url);
            case "UnauthorizedException":
                throw new UnauthorizedException(message, url);
            case "EntityNotFoundException":
                throw new EntityNotFoundException(message, url);
            case "EndpointNotFoundException":
                throw new EndpointNotFoundException(message, url);
            case "UnsupportedVersionException":
                throw new UnsupportedVersionException(message, url);
            case "EntityAlreadyExistsException":
                throw new EntityAlreadyExistsException(message, url);
            case "ConcurrentModificationException":
                throw new ConcurrentModificationException(message, url);
            case "ConstraintViolationException":
                throw new ConstraintViolationException(message, url);
            case "PublishedSurveyException":
                throw new PublishedSurveyException(message, url);
            case "InvalidEntityException":
                Map<String, List<String>> errors = Maps.newHashMap();
                if (node != null && node.getAsJsonObject().get("errors") != null) {
                    errors = RestUtils.GSON.fromJson(node.getAsJsonObject().get("errors"), ERRORS_MAP_TYPE_TOKEN);
                }
                throw new InvalidEntityException(message, errors, url);
            case "NotImplementedException":
                throw new NotImplementedException(message, url);
            case "BadRequestException":
                throw new BadRequestException(message, url);
            default:
                throw new BridgeSDKException(message, statusCode, url);
        }
    }
}
