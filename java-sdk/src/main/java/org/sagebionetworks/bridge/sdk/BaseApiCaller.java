package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.sagebionetworks.bridge.sdk.exceptions.BadRequestException;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.exceptions.ConcurrentModificationException;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.sdk.exceptions.NotAuthenticatedException;
import org.sagebionetworks.bridge.sdk.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.exceptions.UnsupportedVersionException;
import org.sagebionetworks.bridge.sdk.models.upload.UploadRequest;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class BaseApiCaller {

    private static final Logger logger = LoggerFactory.getLogger(BaseApiCaller.class);

    private static final String BRIDGE_API_STATUS_HEADER = "Bridge-Api-Status";
    private static final String BRIDGE_SESSION_HEADER = "Bridge-Session";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String CONNECTION_FAILED = "Connection to server failed or aborted.";
    protected static final String CANNOT_BE_NULL = "%s cannot be null.";
    protected static final String CANNOT_BE_BLANK = "%s cannot be null, an empty string, or whitespace.";
    
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

    @SuppressWarnings("deprecation")
    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
            .setSslcontext(getSSLContext())
            .build();
    private final Executor exec = Executor.newInstance(client);

    private final ObjectMapper mapper = Utilities.getMapper();

    protected BridgeSession session;

    protected final Config config = ClientProvider.getConfig();

    protected BaseApiCaller(BridgeSession session) {
        //checkNotNull(session); no session when used for sign in/sign out/reset password
        this.session = session;
    }

    protected HttpResponse publicGet(String url) {
        url = getFullUrl(url);
        try {

            logger.debug("GET {}", url);
            Request request = Request.Get(url);
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (ClientProtocolException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected HttpResponse s3Put(String url, HttpEntity entity, UploadRequest uploadRequest) {
        try {

            logger.debug("PUT {}\n    <BINARY DATA>", url);
            Request request = Request.Put(url).body(entity);
            request.addHeader("Content-Type", uploadRequest.getContentType());
            request.addHeader("Content-MD5", uploadRequest.getContentMd5());
            HttpResponse response = request.execute().returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (ClientProtocolException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected HttpResponse get(String url) {
        url = getFullUrl(url);
        try {

            Request request = Request.Get(url);
            addApplicationHeaders(request);
            logger.debug("GET {}", url);
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (ClientProtocolException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected <T> T get(String url, TypeReference<T> type) {
        HttpResponse response = get(url);
        return getResponseBodyAsType(response, type);
    }

    protected <T> T get(String url, Class<T> clazz) {
        HttpResponse response = get(url);
        return getResponseBodyAsType(response, clazz);
    }

    protected HttpResponse post(String url) {
        url = getFullUrl(url);
        try {

            Request request = Request.Post(url);
            addApplicationHeaders(request);
            logger.debug("POST {}\n    <EMPTY>", url);
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (ClientProtocolException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected HttpResponse post(String url, Object object) {
        try {
            return postJSON(url, mapper.writeValueAsString(object));

        } catch (JsonProcessingException e) {
            String message = String.format("Could not process %s: %s", object.getClass().getSimpleName(), object.toString());
            throw new BridgeSDKException(message, e);
        }
    }

    protected <T> T post(String url, Object object, TypeReference<T> type) {
        try {

            String json = mapper.writeValueAsString(object);
            HttpResponse response = postJSON(url, json);
            return getResponseBodyAsType(response, type);

        } catch (JsonProcessingException e) {
            String message = String.format("Could not process %s: %s", object.getClass().getSimpleName(), object.toString());
            throw new BridgeSDKException(message, e);
        }
    }

    protected <T> T post(String url, Object object, Class<T> clazz) {
        try {

            String json = (object != null) ? mapper.writeValueAsString(object) : null;
            HttpResponse response = postJSON(url, json);
            return (clazz != null) ? getResponseBodyAsType(response, clazz) : null;

        } catch (JsonProcessingException e) {
            String message = String.format("Could not process %s: %s", object.getClass().getSimpleName(), object.toString());
            throw new BridgeSDKException(message, e);
        }
    }

    private HttpResponse postJSON(String url, String json) {
        url = getFullUrl(url);
        try {

            Request request = Request.Post(url);
            addApplicationHeaders(request);
            if (json != null) {
                request.bodyString(json, ContentType.APPLICATION_JSON);
                logger.debug("POST {} \n     {}", url, maskPassword(json));
            } else {
                logger.debug("POST {}", url);
            }
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected HttpResponse delete(String url) {
        url = getFullUrl(url);

        try {
            Request request = Request.Delete(url);
            addApplicationHeaders(request);
            logger.debug("DELETE {}", url);
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;

        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
        }
    }

    protected String getResponseBody(HttpResponse response) {
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e);
        }
    }


    private void addApplicationHeaders(Request request) {
        logger.info("User-Agent: " + ClientProvider.getClientInfo().toString());
        request.setHeader(USER_AGENT_HEADER, ClientProvider.getClientInfo().toString());
        if (session != null && session.isSignedIn()) {
            request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
        }
    }

    private <T> T getResponseBodyAsType(HttpResponse response, Class<T> c) {
        String responseBody = getResponseBody(response);
        try {
            return mapper.readValue(responseBody, c);
        } catch (IOException e) {
            throw new BridgeSDKException("Error message: " + e.getMessage()
                    + "\nSomething went wrong while converting Response Body JSON into " + c.getSimpleName()
                    + ": responseBody=" + responseBody, e);
        }
    }

    private <T> T getResponseBodyAsType(HttpResponse response, TypeReference<T> type) {
        String responseBody = getResponseBody(response);
        try {
            return mapper.readValue(responseBody, type);
        } catch (IOException e) {
            throw new BridgeSDKException("Error message: " + e.getMessage()
                    + "\nSomething went wrong while converting Response Body JSON into " + type.getType().getClass().getSimpleName()
                    + ": responseBody=" + responseBody, e);
        }
    }

    private JsonNode getJsonNode(HttpResponse response) {
        try {
            return mapper.readTree(getResponseBody(response));
        } catch (IOException e) {
            // NOTE: It turns out the most likely reason for this is that bad JSON was posted to the
            // server to start with, which causes play to return an HTML page... which causes this
            // method to fail because it's not JSON.
            throw new BridgeSDKException("A problem occurred while processing the response body.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void throwExceptionOnErrorStatus(HttpResponse response, String url) {
        try {
            logger.debug("{} RESPONSE: {}", response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        } catch(IOException e) {
            logger.debug("{} RESPONSE: <ERROR>", response.getStatusLine().getStatusCode());
        }

        // Deprecation warning
        Header[] statusHeaders = response.getHeaders(BRIDGE_API_STATUS_HEADER);
        if (statusHeaders.length > 0 && "deprecated".equals(statusHeaders[0].getValue())) {
            logger.warn(url + " is a deprecated API. This API may return 410 (Gone) at a future date. Please consult the API documentation for an alternative.");
        }

        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            BridgeSDKException e = null;
            try {
                JsonNode node = getJsonNode(response);

                // Not having a message is actually pretty bad
                String message = "There has been an error on the server";
                if (node.has("message")) {
                    message = node.get("message").asText();
                }
                if (statusCode == 401) {
                    e = new NotAuthenticatedException(message, url);
                } else if (statusCode == 403) {
                    e = new UnauthorizedException(message, url);
                } else if (statusCode == 404 && message.length() > "not found.".length()) {
                    e = new EntityNotFoundException(message, url);
                } else if (statusCode == 410) {
                    e = new UnsupportedVersionException(message, url);
                } else if (statusCode == 412) {
                    UserSession userSession = getResponseBodyAsType(response, UserSession.class);
                    e = new ConsentRequiredException("Consent required.", url, new BridgeSession(userSession));
                } else if (statusCode == 409 && message.contains("already exists")) {
                    e = new EntityAlreadyExistsException(message, url);
                } else if (statusCode == 409 && message.contains("has the wrong version number")) {
                    e = new ConcurrentModificationException(message, url);
                } else if (statusCode == 400 && message.contains("A published survey")) {
                    e = new PublishedSurveyException(message, url);
                } else  if (statusCode == 400 && node.has("errors")) {
                    Map<String, List<String>> errors = (Map<String, List<String>>) mapper.convertValue(
                            node.get("errors"), new TypeReference<HashMap<String, ArrayList<String>>>() {});
                    e = new InvalidEntityException(message, errors, url);
                } else if (statusCode == 400) {
                    e = new BadRequestException(message, url);
                } else {
                    e = new BridgeSDKException(message, status.getStatusCode(), url);
                }
            } catch(Throwable t) {
                t.printStackTrace();
                throw new BridgeSDKException(status.getReasonPhrase(), status.getStatusCode(), url);
            }
            throw e;
        }
    }

    private String getFullUrl(String url) {
        assert url != null;
        
        String fullUrl = config.getEnvironment().getUrl() + url;
        assert Utilities.isValidUrl(fullUrl) : fullUrl;
        return fullUrl;
    }

    /**
     * This method creates a query string for a URL
     * (ex: https://api.sagebridge.org/admin/v1/users?email=foo%40bar.com&asdf=qwerty).
     * Specifically, this method generates and returns the "?email=foo%40bar.com&asdf=qwerty" part. The query string
     * parameters are also URL encoded, as per HTTP standard. If any characters need to be encoded, they are encoded
     * using UTF-8.
     *
     * @param parameters
     *         query parameter map
     * @return encoded query param string, which is everything after and including the '?'
     */
    protected String toQueryString(Map<String,String> parameters) {
        checkNotNull(parameters);

        List<String> list = Lists.newArrayList();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String encodedParamValue;
            try {
                encodedParamValue = URLEncoder.encode(entry.getValue(), Charsets.UTF_8.name());
            } catch (UnsupportedEncodingException ex) {
                // If UTF-8 stops being a supported encoding, we have bigger problems than a try-catch block can handle.
                throw new RuntimeException("UTF-8 is not supported on this device");
            }

            list.add(entry.getKey() + "=" + encodedParamValue);
        }
        return "?" + Joiner.on("&").join(list);
    }

    private String maskPassword(String string) {
        return string.replaceAll("password\":\"([^\"]*)\"", "password\":\"[REDACTED]\"");
    }

}