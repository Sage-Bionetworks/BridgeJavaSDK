package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.exceptions.InvalidEntityException;
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

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseInterceptor.class);
    private static final String EXCEPTION_PACKAGE = "org.sagebionetworks.bridge.rest.exceptions.";
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

        if ("InvalidEntityException".equals(type)) {
            Map<String, List<String>> errors = Maps.newHashMap();
            if (node != null && node.getAsJsonObject().get("errors") != null) {
                errors = RestUtils.GSON.fromJson(node.getAsJsonObject().get("errors"), ERRORS_MAP_TYPE_TOKEN);
            }
            throw new InvalidEntityException(message, errors, url);
        }
        
        throwBridgeExceptionIfItExists(type, message, url);

        throw new BridgeSDKException(message, statusCode, url);
    }

    @SuppressWarnings("unchecked")
    private void throwBridgeExceptionIfItExists(String type, String message, String url) {
        if (type != null && !"Unknown".equals(type)) {
            try {
                
                Class<? extends BridgeSDKException> clazz = (Class<? extends BridgeSDKException>) Class
                        .forName(EXCEPTION_PACKAGE + type);
                Constructor<? extends BridgeSDKException> constructor = clazz.getConstructor(String.class, String.class);
                BridgeSDKException exception = constructor.newInstance(message, url);
                throw exception;
                
            } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | 
                    IllegalAccessException | InstantiationException e) {
                logger.debug("Exception instantiating error payload's type: " + type, e);
            }
        }
    }
}
