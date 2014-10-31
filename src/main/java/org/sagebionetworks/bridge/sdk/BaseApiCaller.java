package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

abstract class BaseApiCaller {

    private static final Logger logger = LoggerFactory.getLogger(BaseApiCaller.class);

    private static final String BRIDGE_SESSION_HEADER = "Bridge-Session";
    private static final String CONNECTION_FAILED = "Connection to server failed or aborted.";

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

    protected final ObjectMapper mapper = Utilities.getMapper();

    protected final Config config = ClientProvider.getConfig();

    private Session session;

    BaseApiCaller(Session session) {
        this.session = session;
    }

    protected HttpResponse publicGet(String url) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            logger.debug("GET " + fullUrl);
            Request request = Request.Get(fullUrl);
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected HttpResponse get(String url) {
        return get(url, null);
    }

    protected HttpResponse get(String url, Map<String,String> queryParameters) {
        if (queryParameters != null) {
            url += addQueryParameters(queryParameters);
        }
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Get(fullUrl);
            if (session != null && session.isSignedIn()) {
                request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
            }
            logger.debug("GET " + fullUrl);
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected HttpResponse put(String url, String content, ContentType contentType) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Put(fullUrl).bodyString(content, contentType);
            if (session != null && session.isSignedIn()) {
                request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
            }
            logger.debug("PUT " + fullUrl + "\n    " + content);
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected HttpResponse post(String url) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Post(fullUrl);
            if (session != null && session.isSignedIn()) {
                request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
            }
            logger.debug("POST " + fullUrl + "\n    <EMPTY>");
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (ClientProtocolException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected HttpResponse post(String url, String json) {
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Post(fullUrl).bodyString(json, ContentType.APPLICATION_JSON);
            if (session != null && session.isSignedIn()) {
                request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
            }
            // expensive, don't do it unless necessary
            if (logger.isDebugEnabled()) {
                logger.debug("POST " + fullUrl + "\n    " + maskPassword(json));
            }
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected HttpResponse delete(String url) {
        return delete(url, null);
    }

    protected HttpResponse delete(String url, Map<String,String> queryParameters) {
        if (queryParameters != null) {
            url += addQueryParameters(queryParameters);
        }
        String fullUrl = getFullUrl(url);
        HttpResponse response = null;
        try {
            Request request = Request.Delete(fullUrl);
            if (session != null && session.isSignedIn()) {
                request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
            }
            logger.debug("DELETE " + fullUrl);
            response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, fullUrl);
        } catch (IOException e) {
            throw new BridgeServerException(CONNECTION_FAILED, e, fullUrl);
        }
        return response;
    }

    protected String getSessionToken(HttpResponse response, String url) {
        if (response == null) {
            throw new IllegalArgumentException("HttpResponse object is null.");
        } else if (response.containsHeader(BRIDGE_SESSION_HEADER)) {
            return response.getFirstHeader(BRIDGE_SESSION_HEADER).getValue();
        } else {
            throw new IllegalStateException("Session Token does not exist in this response.");
        }
    }

    protected String getResponseBody(HttpResponse response) {
        String responseBody;
        try {
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e);
        }
        return responseBody;
    }

    protected <T> T getResponseBodyAsType(HttpResponse response, Class<T> c) {
        String responseBody = getResponseBody(response);
        Object obj;
        try {
            obj = mapper.readValue(responseBody, c);
        } catch (IOException e) {
            throw new BridgeSDKException("Error message: " + e.getMessage()
                    + "\nSomething went wrong while converting Response Body Json into " + c.getSimpleName()
                    + ": responseBody=" + responseBody, e);
        }
        return c.cast(obj);
    }

    protected JsonNode getJsonNode(HttpResponse response) {
        JsonNode json;
        try {
            json = mapper.readTree(getResponseBody(response));
        } catch (IOException e) {
            throw new BridgeSDKException("A problem occurred while processing the response body.", e);
        }
        return json;
    }

    protected JsonNode getPropertyFromResponse(HttpResponse response, String property) {
        JsonNode json = getJsonNode(response);
        checkArgument(json.has(property), "JSON from server does not contain the property: " + property);
        return json.get(property);
    }

    @SuppressWarnings("unchecked")
    private void throwExceptionOnErrorStatus(HttpResponse response, String url) {
        try {
            logger.debug(response.getStatusLine().getStatusCode() + " RESPONSE: " + EntityUtils.toString(response.getEntity()));
        } catch(IOException e) {
            logger.debug(response.getStatusLine().getStatusCode() + " RESPONSE: <ERROR>");
        }

        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            BridgeServerException e = null;
            try {
                JsonNode node = getJsonNode(response);
                logger.debug("Error " + response.getStatusLine().getStatusCode() + ": " + node.toString());

                // Not having a message is actually pretty bad
                String message = "There has been an error on the server";
                if (node.has("message")) {
                    message = node.get("message").asText();
                }
                if (node.has("errors")) {
                    Map<String, List<String>> errors = (Map<String, List<String>>) mapper.convertValue(
                            node.get("errors"), new TypeReference<HashMap<String, ArrayList<String>>>() {});
                    e = new InvalidEntityException(message, errors, url);
                } else {
                    e = new BridgeServerException(message, status.getStatusCode(), url);
                }
            } catch(Throwable t) {
                throw new BridgeServerException(status.getReasonPhrase(), status.getStatusCode(), url);
            }
            throw e;
        }
    }

    private String getFullUrl(String url) {
        assert url != null;
        String fullUrl = config.getHost() + url;
        assert utils.isValidUrl(fullUrl) : fullUrl;

        return fullUrl;
    }

    private String addQueryParameters(Map<String,String> parameters) {
        assert parameters != null;

        List<String> list = Lists.newArrayList();
        for (String parameter : parameters.keySet()) {
            list.add(parameter + "=" + parameters.get(parameter));
        }
        return "?" + Joiner.on("&").join(list);
    }

    private String maskPassword(String string) {
        return string.replaceAll("password\":\"([^\"]*)\"", "password\":\"[REDACTED]\"");
    }

}