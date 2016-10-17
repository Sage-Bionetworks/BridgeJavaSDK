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

public class ErrorResponseInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseInterceptor.class);
    
    private static final TypeReference<HashMap<String, ArrayList<String>>> ERRORS_MAP_TYPE_REF = 
            new TypeReference<HashMap<String, ArrayList<String>>>() {};
    private static final String BRIDGE_API_STATUS_HEADER = "Bridge-Api-Status";
    private static final ObjectMapper MAPPER = Utilities.getObjectMapper();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        Response response = chain.proceed(request);
        
        logDeprecationWarning(response);
        if (response.code() != 200) {
            throwErrorCodeException(response);
        }
        return response;
    }
    
    /**
     * TODO: This is really a separate interceptor, it's orthogonal to exception handling.
     * @param response
     */
    private void logDeprecationWarning(Response response) {
        List<String> statusHeaders = response.headers().toMultimap().get(BRIDGE_API_STATUS_HEADER);
        if (statusHeaders != null && statusHeaders.size() > 0 && "deprecated".equals(statusHeaders.get(0))) {
            logger.warn(response.request().url().toString()
                    + " is a deprecated API. This API may return 410 (Gone) at a future date. "
                    + "Please consult the API documentation for an alternative.");
        }
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
