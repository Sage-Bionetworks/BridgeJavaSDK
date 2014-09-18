package org.sagebionetworks.bridge.sdk.util;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class BaseClient {

    private static String HOST = HostName.LOCAL;
    private static String API = "api/v1/";
    private static String BASE_URL = HOST + API;

    private static final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();
    private static final Executor exec = Executor.newInstance(client);

    protected static void setHost(String host) {
        if (HostName.isValidHostName(host)) {
            HOST = host;
        } else {
            throw new AssertionError("Argument \"host\" not one of the accepted fields.");
        }
    }
    
    protected Response get(String url) {
        return get(url, null);
    }

    protected static Response get(String url, Header header) {
        Response response = null;
        try {
            Request request = Request.Get(BASE_URL + url);
            if (header != null) {
                request = request.addHeader(header);
            }
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected static Response post(String url, JsonNode json) {
        Response response = null;
        try {
            Request request = Request.Post(BASE_URL + url).bodyString(json.toString(), ContentType.APPLICATION_JSON);
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    protected static String getSessionToken(Response response) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        }
        HttpResponse hr = null;
        try {
            hr = response.returnResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (hr.containsHeader("Bridge-Session")) {
            return hr.getFirstHeader("Bridge-Session").getValue();
        } else {
            throw new AssertionError("Session Token does not exist in this response.");
        }

    }
}
