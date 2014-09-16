package org.sagebionetworks.bridge.sdk.clients;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.databind.JsonNode;

abstract class BaseClient {

    private static final String HOST = "https://pd.sagebase.org/";
    private static final String API = "api/v1/";
    private static final String BASE_URL = HOST + API;

    protected HttpResponse get(String url) {
        return get(url, null);
    }

    protected static HttpResponse get(String url, Header header) {
        HttpResponse response = null;
        try {
            Request request = Request.Get(BASE_URL + url);
            if (header != null) {
                request = request.addHeader(header);
            }
            response = request.execute().returnResponse();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertResponseOK(response);
        return response;
    }

    protected static HttpResponse post(String url, JsonNode json) {
        HttpResponse response = null;
        try {
            response = Request.Post(BASE_URL + url).bodyString(json.asText(), ContentType.APPLICATION_JSON).execute().returnResponse();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertResponseOK(response);
        return response;
    }

    protected static String getSessionToken(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        }
        if (response.containsHeader("Set-Cookie")) {
            return response.getFirstHeader("Set-Cookie").getValue();
        } else if (response.containsHeader("Cookie")) {
            return response.getFirstHeader("Cookie").getValue();
        } else {
            throw new AssertionError("Session Token does not exist in this response.");
        }

    }
    
    protected static void assertResponseOK(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        } else if (response.getStatusLine().getStatusCode() == 401) {
            throw new AssertionError("User is not signed in.");
        } else if (response.getStatusLine().getStatusCode() == 412) {
            throw new AssertionError("User has not yet consented.");
        } else if (response.getStatusLine().getStatusCode() == 500) {
            throw new AssertionError("Something went wrong on the server.");
        }
    }
}
