package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

abstract class BaseApiCaller {

    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
            .build();
    private final Executor exec = Executor.newInstance(client);

    final ClientProvider provider;

    BaseApiCaller(ClientProvider provider) {
        this.provider = provider;
    }

    // Developers may wish to hold on to the client, but if they do this, they'll need access
    // to the provider in order to sign out/sign in again.
    public ClientProvider getProvider() {
        return provider;
    }

    final Response get(String url) {
        Response response = null;
        try {
            Request request = Request.Get(getFullUrl(url));
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    final Response authorizedGet(String url) {
        Response response = null;
        try {
            System.out.println(getFullUrl(url));
            Request request = Request.Get(getFullUrl(url));
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    final Response post(String url) {
        Response response = null;
        try {
            Request request = Request.Post(getFullUrl(url));
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    final Response post(String url, String json) {
        Response response = null;
        try {
            Request request = Request.Post(getFullUrl(url)).bodyString(json, ContentType.APPLICATION_JSON);
            System.out.println(getFullUrl(url));

            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    final String getSessionToken(Response response) {
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

    final String getFullUrl(String url) {
        // TODO: If you put host with/without a slash or do/don't start the URL with a slash
        // you get bad behavior, should use a URL parsing library or something to fix this.
        return "http://" + provider.getHost() + url;
    }
}