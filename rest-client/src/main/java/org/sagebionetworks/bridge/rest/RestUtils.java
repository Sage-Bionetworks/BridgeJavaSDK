package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.rest.api.ForConsentedUsersApi;
import org.sagebionetworks.bridge.rest.api.ForWorkersApi;
import org.sagebionetworks.bridge.rest.api.ParticipantsApi;
import org.sagebionetworks.bridge.rest.gson.ByteArrayToBase64TypeAdapter;
import org.sagebionetworks.bridge.rest.gson.DateTimeTypeAdapter;
import org.sagebionetworks.bridge.rest.gson.LocalDateTypeAdapter;
import org.sagebionetworks.bridge.rest.gson.RuntimeTypeAdapterFactory;
import org.sagebionetworks.bridge.rest.model.ABTestScheduleStrategy;
import org.sagebionetworks.bridge.rest.model.AccountSummaryList;
import org.sagebionetworks.bridge.rest.model.BloodPressureConstraints;
import org.sagebionetworks.bridge.rest.model.BooleanConstraints;
import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.rest.model.Constraints;
import org.sagebionetworks.bridge.rest.model.CriteriaScheduleStrategy;
import org.sagebionetworks.bridge.rest.model.DateConstraints;
import org.sagebionetworks.bridge.rest.model.DateTimeConstraints;
import org.sagebionetworks.bridge.rest.model.DecimalConstraints;
import org.sagebionetworks.bridge.rest.model.DurationConstraints;
import org.sagebionetworks.bridge.rest.model.HeightConstraints;
import org.sagebionetworks.bridge.rest.model.IntegerConstraints;
import org.sagebionetworks.bridge.rest.model.MultiValueConstraints;
import org.sagebionetworks.bridge.rest.model.ScheduleStrategy;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.SimpleScheduleStrategy;
import org.sagebionetworks.bridge.rest.model.StringConstraints;
import org.sagebionetworks.bridge.rest.model.SurveyElement;
import org.sagebionetworks.bridge.rest.model.SurveyInfoScreen;
import org.sagebionetworks.bridge.rest.model.SurveyQuestion;
import org.sagebionetworks.bridge.rest.model.TimeConstraints;
import org.sagebionetworks.bridge.rest.model.UploadRequest;
import org.sagebionetworks.bridge.rest.model.UploadSession;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;
import org.sagebionetworks.bridge.rest.model.WeightConstraints;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Utilities for working with the REST model objects returned from the Bridge REST client.
 */
public class RestUtils {
    private static final Joiner JOINER = Joiner.on(",");
    private static final Predicate<String> LANG_PREDICATE = Predicates.and(Predicates.notNull(),
            Predicates.containsPattern(".+"));
    private static final String BRIDGE_UPLOAD_MIME_TYPE = "application/zip";

    // It's unfortunate but we need to specify subtypes for GSON.

    private static final RuntimeTypeAdapterFactory<SurveyElement> surveyElementFactory = RuntimeTypeAdapterFactory  
            .of(SurveyElement.class, "type")
            .registerSubtype(SurveyQuestion.class, SurveyQuestion.class.getSimpleName())
            .registerSubtype(SurveyInfoScreen.class, SurveyInfoScreen.class.getSimpleName());
    
    private static final RuntimeTypeAdapterFactory<ScheduleStrategy> scheduleStrategyFactory = RuntimeTypeAdapterFactory  
            .of(ScheduleStrategy.class, "type")
            .registerSubtype(SimpleScheduleStrategy.class, SimpleScheduleStrategy.class.getSimpleName())
            .registerSubtype(ABTestScheduleStrategy.class, ABTestScheduleStrategy.class.getSimpleName())
            .registerSubtype(CriteriaScheduleStrategy.class, CriteriaScheduleStrategy.class.getSimpleName());
    
    private static final RuntimeTypeAdapterFactory<Constraints> constraintsFactory = RuntimeTypeAdapterFactory  
            .of(Constraints.class, "type")
            .registerSubtype(BooleanConstraints.class, BooleanConstraints.class.getSimpleName())
            .registerSubtype(DateConstraints.class, DateConstraints.class.getSimpleName())
            .registerSubtype(DateTimeConstraints.class, DateTimeConstraints.class.getSimpleName())
            .registerSubtype(DecimalConstraints.class, DecimalConstraints.class.getSimpleName())
            .registerSubtype(DurationConstraints.class, DurationConstraints.class.getSimpleName())
            .registerSubtype(IntegerConstraints.class, IntegerConstraints.class.getSimpleName())
            .registerSubtype(MultiValueConstraints.class, MultiValueConstraints.class.getSimpleName())
            .registerSubtype(StringConstraints.class, StringConstraints.class.getSimpleName())
            .registerSubtype(TimeConstraints.class, TimeConstraints.class.getSimpleName())
            .registerSubtype(BloodPressureConstraints.class, BloodPressureConstraints.class.getSimpleName())
            .registerSubtype(HeightConstraints.class, HeightConstraints.class.getSimpleName())
            .registerSubtype(WeightConstraints.class, WeightConstraints.class.getSimpleName());
    
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapterFactory(surveyElementFactory)
            .registerTypeAdapterFactory(scheduleStrategyFactory)
            .registerTypeAdapterFactory(constraintsFactory)
            .create();
    
    static <T> T last(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list.get(list.size()-1);
    }
    
    /**
     * Downcast an object to a specific subtype where GSON does not know the type during 
     * deserialization. In that case, GSON uses a generic Map structure to hold the data.
     * This can convert that generic representation into a specific type.
     * @param <T> the target type of the cast 
     * @param object - an object to cast to the supplied type
     * @param type - the target type of the cast
     * @return the object to downcast to the supplied type
     */
    public static <T> T toType(Object object, Class<T> type) {
        return GSON.fromJson(GSON.toJson(object), type);
    }
    
    /**
     * Are all required consents signed?
     * @param session
     *      A UserSessionInfo object
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
            if (isTrue(status.isRequired()) && !isTrue(status.isConsented())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Are all the required consents up-to-date?
     * @param session
     *      A UserSessionInfo object
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
            if (isTrue(status.isRequired()) && !isTrue(status.isSignedMostRecentConsent())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>Convert ClientInfo object into a User-Agent header value that can be used by the Bridge 
     * server to filter content appropriately for your app.</p>
     * 
     * <p>There are three main stanzas in the Bridge User-Agent header, and all parts of a given 
     * stanza must be provided or that stanza will be dropped from the final header:</p>
     * 
     *  <ul>
     *   <li>appName and appVersion;</li>
     *   <li>deviceName, osName and osVersion;</li>
     *   <li>sdkName and sdkVersion</li>
     *  </ul>
     *  
     *  <p>The ClientManager will provide values for the final two groupings (and enforces 
     *  settings for the SDK information). </p>
     * 
     * @param info
     *      a ClientInfo object
     * @return
     *  string User-Agent header value
     */
    public static String getUserAgent(ClientInfo info) {
        if (info == null) {
            return null;
        }
        // Send what is available. The server can handle all meaningful combinations.
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(info.getAppName()) || info.getAppVersion() != null) {
            if (isNotBlank(info.getAppName())) {
                sb.append(info.getAppName());
            }
            if (info.getAppVersion() != null) {
                sb.append("/");
                sb.append(info.getAppVersion());
            }
        }
        if (isNotBlank(info.getDeviceName()) || isNotBlank(info.getOsName()) || isNotBlank(info.getOsVersion())) {
            sb.append(" (");
            // Only include device name if there's also os, this is one ambiguity we cannot parse.
            if (isNotBlank(info.getDeviceName())) {
                sb.append(info.getDeviceName());
                sb.append("; ");
            }
            if (isNotBlank(info.getOsName())) {
                sb.append(info.getOsName());
            }
            if (isNotBlank(info.getOsVersion())) {
                sb.append("/");
                sb.append(info.getOsVersion());
            }
            sb.append(")");
        }
        if (isNotBlank(info.getSdkName()) || info.getSdkVersion() != null) {
            sb.append(" ");
            if (isNotBlank(info.getSdkName())) {
                sb.append(info.getSdkName());
            }
            if (info.getSdkVersion() != null) {
                sb.append("/");
                sb.append(info.getSdkVersion());
            }
        }
        if ("".equals(sb.toString())) {
            return null;
        }
        return sb.toString();
    }
    
    public static String getAcceptLanguage(List<String> langs) {
        if (langs == null) {
            return null;
        }
        langs = FluentIterable.from(langs).filter(LANG_PREDICATE).toList();
        if (langs.isEmpty()) {
            return null;
        }
        return JOINER.join(langs);
    }

    /**
     * Manages the conversation with the Bridge server and Amazon's S3 service to upload an encrypted zip
     * file to Bridge. This completes the upload in asynchronous mode and returns the UploadSession.
     *
     * @param usersApi
     *      The ForConsentedUsersApi for the user making the upload
     * @param file
     *      A File referencing a zip file to upload
     * @return
     *      An UploadSession with information about the upload session
     * @throws IOException
     *      IOException if an issue occurs during upload. Callers are responsible for re-trying the upload
     */
    public static UploadSession upload(ForConsentedUsersApi usersApi, File file) throws IOException {
        checkNotNull(usersApi, "ForConsentedUsersApi cannot be null");
        checkNotNull(file, "File cannot be null");

        UploadRequest request = makeUploadRequestForFile(file);
        UploadSession session = usersApi.requestUploadSession(request).execute().body();
        uploadToS3(file, session.getUrl());
        usersApi.completeUploadSession(session.getId(), false).execute();
        
        return session;
    }

    /**
     * Creates an upload request for the given file. This method assumes the default content type of "application/zip".
     *
     * @param file
     *         File to upload to Bridge
     * @return the Bridge upload request
     * @throws IOException
     *         IOException if an issue occurs during upload. Callers are responsible for re-trying the upload
     */
    public static UploadRequest makeUploadRequestForFile(File file) throws IOException {
        checkNotNull(file, "File cannot be null");

        // File metadata
        long contentLength = file.length();
        byte[] fileBytes = Files.toByteArray(file);
        String contentMd5 = Base64.encodeBase64String(DigestUtils.md5(fileBytes));

        // Create and return request
        UploadRequest request = new UploadRequest();
        request.setName(file.getName());
        request.setContentLength(contentLength);
        request.setContentMd5(contentMd5);
        request.setContentType(BRIDGE_UPLOAD_MIME_TYPE);

        return request;
    }

    /**
     * Uploads the given file to the given S3 URL. This method assumes the default content type of "application/zip".
     *
     * @param file
     *         File to upload to Bridge
     * @param url
     *         S3 URL to upload to, generally as returned by Bridge Upload Session
     * @throws IOException
     *         IOException if an issue occurs during upload. Callers are responsible for re-trying the upload
     */
    public static void uploadToS3(File file, String url) throws IOException {
        checkNotNull(file, "File cannot be null");
        checkNotNull(url, "url cannot be null");
        checkArgument(!url.isEmpty(), "url cannot be empty");

        // File metadata
        byte[] fileBytes = Files.toByteArray(file);
        String contentMd5 = Base64.encodeBase64String(DigestUtils.md5(fileBytes));

        // Construct S3 client
        URI uri = URI.create(url);
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

        RequestBody body = RequestBody.create(MediaType.parse(BRIDGE_UPLOAD_MIME_TYPE), file);

        // Upload
        s3service.uploadToS3(url, body, contentMd5, BRIDGE_UPLOAD_MIME_TYPE).execute();
    }
    
    /**
     * Makes a defensive copy of SignIn for internal use.
     * <br>
     * We make a defensive copy to 1) ensure object does not change since it is used as the Map's
     * key, and 2) make sure the "type" field is consistent (always null), because type is only
     * settable via reflection
     */
    static SignIn makeInternalCopy(SignIn signIn) {
        SignIn signInKey = null;
        if (signIn != null) {
            signInKey = new SignIn();
            signInKey.email(signIn.getEmail())
                    .password(signIn.getPassword())
                    .study(signIn.getStudy());
        }

        return signInKey;
    }
    
    interface S3Service {
        @PUT
        Call<Void> uploadToS3(@Url String url, @Body RequestBody body, @Header("Content-MD5") String md5Hash,
                @Header("Content-Type") String contentType);
    }
    
    private static boolean isNotBlank(String string) {
        return (string != null && string.length() > 0);
    }
    
    private static boolean isTrue(Boolean bool) {
        return bool != null && bool == Boolean.TRUE;
    }

}
