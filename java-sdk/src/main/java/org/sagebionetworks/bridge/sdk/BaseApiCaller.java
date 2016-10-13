package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

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
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.upload.UploadRequest;
import org.sagebionetworks.bridge.sdk.rest.ApiClientProvider;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import retrofit2.Call;
import retrofit2.Response;

class BaseApiCaller {

    private static final Logger logger = LoggerFactory.getLogger(BaseApiCaller.class);

    private static final String BRIDGE_API_STATUS_HEADER = "Bridge-Api-Status";
    private static final String BRIDGE_SESSION_HEADER = "Bridge-Session";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    private static final String CONNECTION_FAILED = "Connection to server failed or aborted.";
    protected static final String CANNOT_BE_NULL = "%s cannot be null.";
    protected static final String CANNOT_BE_BLANK = "%s cannot be null, an empty string, or whitespace.";
    private static final Joiner JOINER = Joiner.on(", ");
    protected static final ApiClientProvider API_CLIENT_PROVIDER = new ApiClientProvider(
        ClientProvider.getConfig().getEnvironment()
                      .getUrl(),
        ClientProvider.getClientInfo().toString()
    );

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

    private final String studyId;
    protected final SignIn signIn;

    @Deprecated
    protected BridgeSession session;

    protected final Config config = ClientProvider.getConfig();

    protected BaseApiCaller(BridgeSession session) {
        //checkNotNull(session); no session when used for sign in/sign out/reset password
        this.session = session;
        this.studyId = session.getStudyId();
        StudyParticipant studyParticipant = session.getStudyParticipant();
        this.signIn = new SignIn().study(studyId)
                                    .email(studyParticipant.getEmail())
                                    .password(studyParticipant.getPassword());

    }

    protected <T> T call(Call<T> call) {
        try {
            Response<T> response = call.execute();

            throwExceptionOnErrorStatus(response, call.request().url().toString());

            return response.body();
        } catch (IOException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e);
        }
    }

    protected HttpResponse publicGet(String url) {
        url = getFullUrl(url);

        logger.debug("GET {}", url);
        Request request = Request.Get(url);
        return executeRequest(request, url);
    }

    protected HttpResponse s3Put(String url, HttpEntity entity, UploadRequest uploadRequest) {
        logger.debug("PUT {}\n    <BINARY DATA>", url);
        Request request = Request.Put(url).body(entity);
        request.addHeader("Content-Type", uploadRequest.getContentType());
        request.addHeader("Content-MD5", uploadRequest.getContentMd5());

        return executeRequest(request, url);
    }

    protected HttpResponse get(String url) {
        url = getFullUrl(url);

        Request request = Request.Get(url);
        addApplicationHeaders(request);
        logger.debug("GET {}", url);

        return executeRequest(request, url);
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

        Request request = Request.Post(url);
        addApplicationHeaders(request);
        logger.debug("POST {}\n    <EMPTY>", url);

        return executeRequest(request, url);
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
        String json = Utilities.getObjectAsJson(object);
        HttpResponse response = postJSON(url, json);
        return getResponseBodyAsType(response, type);
    }

    protected <T> T post(String url, Object object, Class<T> clazz) {
        String json = (object != null) ? Utilities.getObjectAsJson(object) : null;

        HttpResponse response = postJSON(url, json);
        return getResponseBodyAsType(response, clazz);
    }

    private HttpResponse postJSON(String url, String json) {
        url = getFullUrl(url);

        Request request = Request.Post(url);
        addApplicationHeaders(request);
        if (json != null) {
            request.bodyString(json, ContentType.APPLICATION_JSON);
            logger.debug("POST {} \n     {}", url, maskPassword(json));
        } else {
            logger.debug("POST {}", url);
        }

        return executeRequest(request, url);
    }

    protected HttpResponse delete(String url) {
        url = getFullUrl(url);

        Request request = Request.Delete(url);
        addApplicationHeaders(request);
        logger.debug("DELETE {}", url);

        return executeRequest(request, url);
    }

    private HttpResponse executeRequest(Request request, String url) {
        try {
            HttpResponse response = exec.execute(request).returnResponse();
            throwExceptionOnErrorStatus(response, url);
            return response;
        } catch (ClientProtocolException e) {
            throw new BridgeSDKException(CONNECTION_FAILED, e, url);
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
        if (!ClientProvider.getLanguages().isEmpty()) {
            request.setHeader(ACCEPT_LANGUAGE_HEADER, JOINER.join(ClientProvider.getLanguages()));
        }
        if (session != null && session.isSignedIn()) {
            request.setHeader(BRIDGE_SESSION_HEADER, session.getSessionToken());
        }
    }

    private <T> T getResponseBodyAsType(HttpResponse response, Class<T> c) {
        String responseBody = getResponseBody(response);
        return Utilities.getJsonAsType(responseBody, c);
    }

    private <T> T getResponseBodyAsType(HttpResponse response, TypeReference<T> type) {
        String responseBody = getResponseBody(response);
        return Utilities.getJsonAsType(responseBody, type);
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

  private void throwExceptionOnErrorStatus(Response response, String url) {
      try {
          logger.debug("{} RESPONSE: {}", response.code(), response.raw() + response.raw().body()
              .string());
      } catch(IOException e) {
          logger.debug("{} RESPONSE: <ERROR>", response.code());
      }

    List<String> statusHeaders = response.headers().toMultimap().get(BRIDGE_API_STATUS_HEADER);
    if (statusHeaders.size() > 0 && "deprecated".equals(statusHeaders.get(0))) {
      logger.warn(url + " is a deprecated API. This API may return 410 (Gone) at a future date. Please consult the API documentation for an alternative.");
    }
    if (response.isSuccessful()) {
      return;
    }

    try {
      JsonNode node = mapper.readTree(response.errorBody().string());
        throwExceptionOnErrorStatus(url,response.code(),node);
      } catch(BridgeSDKException e){
        // rethrow known exceptions
        throw e;
      } catch(Throwable t) {
        t.printStackTrace();
        throw new BridgeSDKException(response.message(), response.code(), url);
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
            try {
                JsonNode node = getJsonNode(response);
                throwExceptionOnErrorStatus(url,statusCode,node);
            } catch(BridgeSDKException e){
              // rethrow known exceptions
              throw e;
            } catch(Throwable t) {
                t.printStackTrace();
                throw new BridgeSDKException(status.getReasonPhrase(), status.getStatusCode(), url);
            }
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
            UserSession session = Utilities.getJsonAsType(node.asText(), UserSession.class);
            e = new ConsentRequiredException("Consent required.", url, new BridgeSession
                (session, studyId));
        } else if (statusCode == 409 && message.contains("already exists")) {
            e = new EntityAlreadyExistsException(message, url);
        } else if (statusCode == 409 && message.contains("has the wrong version number")) {
            e = new ConcurrentModificationException(message, url);
        } else if (statusCode == 400 && message.contains("A published survey")) {
            e = new PublishedSurveyException(message, url);
        } else  if (statusCode == 400 && node.has("errors")) {
            Map<String, List<String>> errors = mapper.convertValue(
                node.get("errors"), new TypeReference<HashMap<String, ArrayList<String>>>() {});
            e = new InvalidEntityException(message, errors, url);
        } else if (statusCode == 400) {
            e = new BadRequestException(message, url);
        } else {
            e = new BridgeSDKException(message, statusCode, url);
        }
      throw e;
    }

    private String getFullUrl(String url) {
        assert url != null;

        String fullUrl = config.getEnvironment().getUrl() + url;
        assert Utilities.isValidUrl(fullUrl) : fullUrl;
        return fullUrl;
    }

    private String maskPassword(String string) {
        return string.replaceAll("password\":\"([^\"]*)\"", "password\":\"[REDACTED]\"");
    }

}