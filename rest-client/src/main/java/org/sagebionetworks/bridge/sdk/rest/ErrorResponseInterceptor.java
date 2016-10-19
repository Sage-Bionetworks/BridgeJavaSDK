package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.rest.exceptions.BadRequestException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.ConcurrentModificationException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.NotAuthenticatedException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.rest.exceptions.UnsupportedVersionException;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Convert HTTP bad request and server errors to Java business exceptions.
 *
 */
public class ErrorResponseInterceptor implements Interceptor {

    private static final TypeReference<HashMap<String, ArrayList<String>>> ERRORS_MAP_TYPE_REF = 
            new TypeReference<HashMap<String, ArrayList<String>>>() {};
    private static final ObjectMapper MAPPER = Utilities.getObjectMapper();

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
            JsonNode node = MAPPER.readTree(response.body().string());
            throwExceptionOnErrorStatus(url, response.code(), node);
        } catch (BridgeSDKException e) {
            // rethrow known exceptions
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new BridgeSDKException(response.message(), response.code(), url);
        }        
    }
    
    private void throwExceptionOnErrorStatus(String url, int statusCode, JsonNode node) {
        // Not having a message is actually pretty bad
        String message = "There has been an error on the server";
        if (node.has("message")) {
            message = node.get("message").asText();
        }

        BridgeSDKException e;
        if (statusCode == 401) {
            e = new NotAuthenticatedException(message, url);
        } else if (statusCode == 403) {
            e = new UnauthorizedException(message, url);
        } else if (statusCode == 404 && message.length() > "not found.".length()) {
            e = new EntityNotFoundException(message, url);
        } else if (statusCode == 410) {
            e = new UnsupportedVersionException(message, url);
        } else if (statusCode == 412) {
            UserSessionInfo session = Utilities.getJsonAsType(node, UserSessionInfo.class);
            e = new ConsentRequiredException("Consent required.", url, session);
        } else if (statusCode == 409 && message.contains("already exists")) {
            e = new EntityAlreadyExistsException(message, url);
        } else if (statusCode == 409 && message.contains("has the wrong version number")) {
            e = new ConcurrentModificationException(message, url);
        } else if (statusCode == 400 && message.contains("A published survey")) {
            e = new PublishedSurveyException(message, url);
        } else if (statusCode == 400 && node.has("errors")) {
            Map<String, List<String>> errors = MAPPER.convertValue(node.get("errors"), ERRORS_MAP_TYPE_REF);
            e = new InvalidEntityException(message, errors, url);
        } else if (statusCode == 400) {
            e = new BadRequestException(message, url);
        } else {
            e = new BridgeSDKException(message, statusCode, url);
        }
        throw e;
    }  
    
}
