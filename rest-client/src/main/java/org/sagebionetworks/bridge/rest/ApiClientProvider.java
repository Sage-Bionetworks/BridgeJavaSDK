package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class for creating clients that are correctly configured to communicate with the
 * Bridge server. This class has been designed so that different authentication credentials
 * can be used for different clients retrieved from this provider.
 */
public class ApiClientProvider {

    private final OkHttpClient unauthenticatedOkHttpClient;
    private final Retrofit.Builder retrofitBuilder;
    private final UserSessionInfoProvider userSessionInfoProvider;
    private final Map<SignIn, WeakReference<Retrofit>> authenticatedRetrofits;
    private final Map<SignIn, Map<Class<?>, WeakReference<?>>> authenticatedClients;

    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage) {
        this(baseUrl, userAgent, acceptLanguage, null);
    }

    // allow unit tests to inject a UserSessionInfoProvider
    ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage,
                      UserSessionInfoProvider userSessionInfoProvider) {
        authenticatedRetrofits = Maps.newHashMap();
        authenticatedClients = Maps.newHashMap();

        UserSessionInterceptor sessionInterceptor = new UserSessionInterceptor();

        // Devo may take up to 2 minutes to boot up. Need to set timeouts accordingly so that
        // integration tests succeed
        // May want to make it possible to configure this value when creating the ApiClientProvider.
        unauthenticatedOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(sessionInterceptor)
                .addInterceptor(new HeaderInterceptor(userAgent, acceptLanguage))
                .addInterceptor(new WarningHeaderInterceptor())
                .addInterceptor(new ErrorResponseInterceptor())
                .addInterceptor(new LoggingInterceptor()).build();

        retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl)
                .client(unauthenticatedOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(RestUtils.GSON));

        this.userSessionInfoProvider = userSessionInfoProvider != null ? userSessionInfoProvider
                : new UserSessionInfoProvider(getAuthenticatedRetrofit(null), sessionInterceptor);
    }

    /**
     * @return user session info provider backing this instance
     */
    public UserSessionInfoProvider getUserSessionInfoProvider() {
        return userSessionInfoProvider;
    }

    /**
     * Creates an unauthenticated client.
     *
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @return service client
     */
    public <T> T getClient(Class<T> service) {
        return getClientImpl(service, null);
    }

    /**
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @param signIn
     *         credentials for the user, or null for an unauthenticated client
     * @return service client that is authenticated with the user's credentials
     */
    public <T> T getClient(Class<T> service, SignIn signIn) {
        Preconditions.checkNotNull(signIn);

        return getClientImpl(service, signIn);
    }

    @SuppressWarnings("unchecked")
    private <T> T getClientImpl(Class<T> service, SignIn signIn) {
        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);

        Map<Class<?>, WeakReference<?>> userClients = authenticatedClients.get(internalSignIn);
        if (userClients == null) {
            userClients = Maps.newHashMap();
            authenticatedClients.put(internalSignIn, userClients);
        }

        T authenticateClient = null;
        WeakReference<?> clientReference = userClients.get(service);

        if (clientReference != null) {
            authenticateClient = (T) clientReference.get();
        }

        if (authenticateClient == null) {
            authenticateClient = getAuthenticatedRetrofit(internalSignIn).create(service);
            userClients.put(service, new WeakReference<>(authenticateClient));
        }

        return authenticateClient;
    }

    private Retrofit getAuthenticatedRetrofit(SignIn signIn) {
        Retrofit authenticatedRetrofit = null;
        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);

        WeakReference<Retrofit> authenticatedRetrofitReference = authenticatedRetrofits.get
                (internalSignIn);
        if (authenticatedRetrofitReference != null) {
            authenticatedRetrofit = authenticatedRetrofitReference.get();
        }

        if (authenticatedRetrofit == null) {
            authenticatedRetrofit = createAuthenticatedRetrofit(internalSignIn, null);
        }

        return authenticatedRetrofit;
    }

    // default access to allow tests to inject retrofit
    Retrofit createAuthenticatedRetrofit(SignIn signIn, AuthenticationHandler handler) {
        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);

        OkHttpClient.Builder httpClientBuilder = unauthenticatedOkHttpClient.newBuilder();

        if (internalSignIn != null) {
            AuthenticationHandler authenticationHandler = handler;
            // this is the normal code path (only tests will inject a handler)
            if (authenticationHandler == null) {
                authenticationHandler = new AuthenticationHandler(internalSignIn,
                        userSessionInfoProvider
                );
            }
            httpClientBuilder.addInterceptor(authenticationHandler).authenticator
                    (authenticationHandler);
        }

        Retrofit authenticatedRetrofit = retrofitBuilder.client(httpClientBuilder.build()).build();
        authenticatedRetrofits.put(internalSignIn,
                new WeakReference<>(authenticatedRetrofit));

        return authenticatedRetrofit;

    }

    // allow test access to retrofit builder
    Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

}
