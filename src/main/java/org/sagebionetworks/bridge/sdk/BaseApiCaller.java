package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract class BaseApiCaller {

    Utilities utils = Utilities.getInstance();

    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
            .build();
    private final Executor exec = Executor.newInstance(client);

    final ObjectMapper mapper = Utilities.getMapper();

    final ClientProvider provider;

    BaseApiCaller(ClientProvider provider) {
        this.provider = provider;
    }

    // Developers may wish to hold on to the client, but if they do this, they'll need access
    // to the provider in order to sign out/sign in again.
    public ClientProvider getProvider() {
        return provider;
    }

    final HttpResponse get(String url) {
        HttpResponse response = null;
        try {
            Request request = Request.Get(url);
            response = exec.execute(request).returnResponse();
        } catch (ClientProtocolException e) {
            throw constructServerException(e, response);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e);
        }
        return response;
    }

    final HttpResponse authorizedGet(String url) {
        HttpResponse response = null;
        try {
            Request request = Request.Get(url);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
        } catch (ClientProtocolException e) {
            throw constructServerException(e, response);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e);
        }
        return response;
    }

    final HttpResponse post(String url) {
        HttpResponse response = null;
        try {
            Request request = Request.Post(url);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
        } catch (ClientProtocolException e) {
            throw constructServerException(e, response);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e);
        }

        return response;
    }

    final HttpResponse post(String url, String json) {
        HttpResponse response = null;
        try {
            Request request = Request.Post(url).bodyString(json, ContentType.APPLICATION_JSON);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
        } catch (ClientProtocolException e) {
            throw constructServerException(e, response);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e);
        }

        return response;
    }

    final HttpResponse delete(String url) {
        HttpResponse response = null;
        try {
            Request request = Request.Delete(url);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
        } catch (ClientProtocolException e) {
            throw constructServerException(e, response);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e);
        }

        return response;
    }

    final String getSessionToken(HttpResponse response, String url) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        } else if (response.containsHeader("Bridge-Session")) {
            return response.getFirstHeader("Bridge-Session").getValue();
        } else {
            throw new IllegalStateException("Session Token does not exist in this response.");
        }
    }

    final String getFullUrl(String url) {
        assert url != null;
        String fullUrl = provider.getConfig().getHost() + url;
        assert utils.isValidUrl(fullUrl) : fullUrl;

        return fullUrl;
    }

    final String getResponseBody(HttpResponse response) {
        String responseBody;
        try {
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new BridgeSDKException("Couldn't parse server response.", e);
        } catch (IOException e) {
            throw new BridgeSDKException("Something went wrong while reading server response.", e);
        }
        return responseBody;
    }

    final String addQueryParameters(Map<String,String> parameters) {
        assert parameters != null;

        StringBuilder builder = new StringBuilder();
        builder.append("?");
        for (String parameter : parameters.keySet()) {
            builder.append("&" + parameter + "=" + parameters.get(parameter));
        }
        builder.deleteCharAt(1); // remove first ampersand.

        return builder.toString();
    }

    final JsonNode getPropertyFromResponse(HttpResponse response, String property) {
        JsonNode json;
        try {
            System.out.println("Response body: \"" + getResponseBody(response) + "\"");
            System.out.println(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
            json = mapper.readTree(getResponseBody(response));
        } catch (IOException e) {
            throw new BridgeSDKException("A problem occurred while processing the response body.", e);
        }

        assert json.has(property);
        return json.get(property);
    }

    final BridgeServerException constructServerException(Throwable t, HttpResponse hr) {
        assert t != null && hr != null;

        String serverResponse = null;
        try {
            serverResponse = EntityUtils.toString(hr.getEntity());
        } catch (ParseException e) {
            throw new BridgeSDKException("Couldn't parse server response.", e);
        } catch (IOException e) {
            throw new BridgeSDKException("An error occurred while reading server response.", e);
        }

        return new BridgeServerException(t, hr.getStatusLine(), serverResponse);
    }
}