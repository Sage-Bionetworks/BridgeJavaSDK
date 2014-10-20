package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
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
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

abstract class BaseApiCaller {

    Utilities utils = Utilities.valueOf();

    // Create an SSL context that does no certificate validation whatsoever.
    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() { return null; }
    }

    private SSLContext getSSLContext() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
            return ctx;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
            .setSslcontext(getSSLContext())
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

    protected final HttpResponse publicGet(String url) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Get(fullUrl);
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        }
        return response;
    }

    protected final HttpResponse get(String url) {
        return get(url, null);
    }

    protected final HttpResponse get(String url, Map<String,String> queryParameters) {
        if (queryParameters != null) {
            url += addQueryParameters(queryParameters);
        }
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Get(fullUrl);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        }
        return response;
    }

    protected final HttpResponse post(String url) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Post(fullUrl);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        }
        return response;
    }

    protected final HttpResponse post(String url, String json) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Post(fullUrl).bodyString(json, ContentType.APPLICATION_JSON);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        }
        return response;
    }

    protected final HttpResponse delete(String url) {
        return delete(url, null);
    }

    protected final HttpResponse delete(String url, Map<String,String> queryParameters) {
        if (queryParameters != null) {
            url += addQueryParameters(queryParameters);
        }
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Delete(fullUrl);
            request.setHeader("Bridge-Session", provider.getSessionToken());
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException("Connection to server failed or aborted.", e, fullUrl);
        }

        return response;
    }

    protected final String getSessionToken(HttpResponse response, String url) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        } else if (response.containsHeader("Bridge-Session")) {
            return response.getFirstHeader("Bridge-Session").getValue();
        } else {
            throw new IllegalStateException("Session Token does not exist in this response.");
        }
    }

    protected final String getResponseBody(HttpResponse response) {
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

    protected final <T> T getResponseBodyAsType(HttpResponse response, Class<T> c) {
        String responseBody = getResponseBody(response);
        Object obj;
        try {
            obj = mapper.readValue(responseBody, c);
        } catch (IOException e) {
            throw new BridgeSDKException("Something went wrong while converting Response Body Json into " + c.getSimpleName() + ": responseBody="
                    + responseBody, e);
        }
        return c.cast(obj);
    }

    protected final JsonNode getPropertyFromResponse(HttpResponse response, String property) {
        JsonNode json;
        try {
            json = mapper.readTree(getResponseBody(response));
        } catch (IOException e) {
            throw new BridgeSDKException("A problem occurred while processing the response body.", e);
        }

        assert json.has(property);
        return json.get(property);
    }

    private void throwExceptionOnErrorStatus(HttpResponse response, String url) {
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            BridgeServerException e = null;
            try {
                String message = getPropertyFromResponse(response, "message").asText();
                e = new BridgeServerException(message, status.getStatusCode(), url);
            } catch(Throwable t) {
                throw new BridgeServerException(status.getReasonPhrase(), status.getStatusCode(), url);
            }
            throw e;
        }
    }

    private final String getFullUrl(String url) {
        assert url != null;
        String fullUrl = provider.getConfig().getHost() + url;
        assert utils.isValidUrl(fullUrl) : fullUrl;

        return fullUrl;
    }

    private final String addQueryParameters(Map<String,String> parameters) {
        assert parameters != null;

        List<String> list = Lists.newArrayList();
        for (String parameter : parameters.keySet()) {
            list.add(parameter + "=" + parameters.get(parameter));
        }
        return "?" + Joiner.on("&").join(list);
    }
}