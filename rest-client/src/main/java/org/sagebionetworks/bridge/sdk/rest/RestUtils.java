package org.sagebionetworks.bridge.sdk.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import org.sagebionetworks.bridge.sdk.rest.api.ForConsentedUsersApi;
import org.sagebionetworks.bridge.sdk.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.rest.json.LowercaseEnumModule;
import org.sagebionetworks.bridge.sdk.rest.model.ClientInfo;
import org.sagebionetworks.bridge.sdk.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.sdk.rest.model.EmptyPayload;
import org.sagebionetworks.bridge.sdk.rest.model.UploadRequest;
import org.sagebionetworks.bridge.sdk.rest.model.UploadSession;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public class RestUtils {

    public static final ObjectMapper MAPPER = getObjectMapper();
    
    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new LowercaseEnumModule());
        return mapper;
    }
    
    static <T> T getJsonAsType(JsonNode json, Class<T> c) {
        try {
            return MAPPER.treeToValue(json, c);
        } catch (IOException e) {
            String message = "Error message: " + e.getMessage() + 
                    "\nSomething went wrong while converting JSON into " + c.getSimpleName() + ": json=" + json;
            throw new BridgeSDKException(message, e);
        }
    }
    
    static <T> T last(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list.get(list.size()-1);
    }
    
    /**
     * Are all required consents signed?
     * @param session
     * @return true if all required consents are signed, false if there are no consents, 
     *  or they are not all signed
     */
    public static boolean isUserConsented(UserSessionInfo session) {
        checkNotNull(session);
        Map<String,ConsentStatus> statuses = session.getConsentStatuses();
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (isTrue(status.getRequired()) && !isTrue(status.getConsented())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Are all the required consents up-to-date?
     * @param session
     * @return true if all required consents are signed and up-to-date, false if there are no 
     *  consents, or they are not all signed, or they are not all up-to-date.
     */
    public static boolean isConsentCurrent(UserSessionInfo session) {
        checkNotNull(session);
        Map<String,ConsentStatus> statuses = session.getConsentStatuses();
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (isTrue(status.getRequired()) && !isTrue(status.getSignedMostRecentConsent())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Convert ClientInfo object into a User-Agent header value that can be used by the Bridge 
     * server to filter content appropriately for your app.
     * @param info
     * @return
     *  string User-Agent header value
     */
    public static String getUserAgent(ClientInfo info) {
        List<String> stanzas = Lists.newArrayListWithCapacity(3);
        if (isNotBlank(info.getAppName()) && info.getAppVersion() != null) {
            stanzas.add(String.format("%s/%s", info.getAppName(), info.getAppVersion()));
        }
        if (isNotBlank(info.getDeviceName()) && isNotBlank(info.getOsName()) && isNotBlank(info.getOsVersion())) {
            stanzas.add(String.format("(%s; %s/%s)", info.getDeviceName(), info.getOsName(), info.getOsVersion()));
        } else if (isNotBlank(info.getOsName()) && isNotBlank(info.getOsVersion())) {
            stanzas.add(String.format("(%s/%s)", info.getOsName(), info.getOsVersion()));
        }
        if (!stanzas.isEmpty() && isNotBlank(info.getSdkName()) && info.getSdkVersion() != null) {
            stanzas.add(String.format("%s/%s", info.getSdkName(), info.getSdkVersion()));    
        }
        if (stanzas.isEmpty()) {
            throw new BridgeSDKException("ClientInfo provided without enough information for User-Agent string: " + info, 500);
        }
        return Joiner.on(" ").join(stanzas);
    }
    
    private static boolean isNotBlank(String string) {
        return (string != null && string.length() > 0);
    }
    
    private static boolean isTrue(Boolean bool) {
        return bool != null && bool == Boolean.TRUE;
    }
    
    interface S3Service {
        @PUT
        Call<Void> uploadToS3(@Url String url, @Body RequestBody body, @Header("Content-MD5") String md5Hash,
                @Header("Content-Type") String contentType);
    }
    
    public static UploadSession upload(ForConsentedUsersApi usersApi, File file) throws IOException {
        checkNotNull(usersApi, "ForConsentedUsersApi cannot be null");
        checkNotNull(file, "File cannot be null");
        
        long contentLength = file.length();
        byte[] fileBytes = Files.toByteArray(file);
        String contentMd5 = Base64.encodeBase64String(DigestUtils.md5(fileBytes));
        
        UploadRequest request = new UploadRequest();
        request.setName(file.getName());
        request.setContentLength((int)contentLength);
        request.setContentMd5(contentMd5);
        request.setContentType("application/zip"); // TODO: Always?!
        
        UploadSession session = usersApi.requestUploadSession(request).execute().body();
        
        URI uri = URI.create(session.getUrl());
        String baseUrl = uri.getScheme()+"://"+uri.getHost()+"/";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ErrorResponseInterceptor())
                .addInterceptor(new LoggingInterceptor())
                .retryOnConnectionFailure(false).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client).build();

        S3Service s3service = retrofit.create(S3Service.class);

        RequestBody body = RequestBody.create(MediaType.parse(request.getContentType()), file);

        s3service.uploadToS3(session.getUrl(), body, request.getContentMd5(), request.getContentType()).execute();
        usersApi.completeUploadSession(session.getId(), new EmptyPayload()).execute();
        
        return session;
    }    
}
