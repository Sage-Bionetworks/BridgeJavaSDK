package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.Phone;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

/**
 * Base class for creating clients that are correctly configured to communicate with the
 * Bridge server.
 */
public class ApiClientProvider {
    private static final Interceptor WARNING_INTERCEPTOR = new WarningHeaderInterceptor();
    private static final Interceptor ERROR_INTERCEPTOR = new ErrorResponseInterceptor();
    private static final Interceptor LOGGING_INTERCEPTOR = new LoggingInterceptor();

    private final String baseUrl;
    private final String userAgent;
    private final String acceptLanguage;
    private final String appId;
    private final Retrofit unauthenticatedRetrofit;
    private final LoadingCache<Class<?>, ?> unauthenticatedServices;
    private final AuthenticationApi authenticationApi;
    private final SocketFactory socketFactory;
    private final ImmutableList<Interceptor> networkInterceptors;
    private final ImmutableList<Interceptor> applicationInterceptors;

    /**
     * Creates a builder for accessing services associated with an environment and app.
     *
     * @param baseUrl base url for Bridge service
     * @param userAgent
     *         user-agent string in Bridge's expected format, see {@link RestUtils#getUserAgent(ClientInfo)}
     * @param acceptLanguage
     *         optional comma-separated list of preferred languages for this client (most to least
     *         preferred
     * @param appId
     *         app identifier
     */
    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage, String appId) {
        this(baseUrl, userAgent, acceptLanguage, appId, null, Collections.<Interceptor>emptyList(),
                Collections.<Interceptor>emptyList());
    }

    /**
     * Creates a builder for accessing services associated with an environment and appId.
     *
     * @param baseUrl base url for Bridge service
     * @param userAgent
     *         user-agent string in Bridge's expected format, see {@link RestUtils#getUserAgent(ClientInfo)}
     * @param acceptLanguage
     *         optional comma-separated list of preferred languages for this client (most to least
     *         preferred
     * @param appId
     *         app identifier
     * @param socketFactory
     *         optional factory to customize how OkHttp creates sockets. This is used on Android to associate a
     *         socket with statistics for the current thread, as required by Android O. If no factory is passed, the
     *         result is OkHttp's default behavior
     * @param networkInterceptors
     *         additional network applicationInterceptors
     * @param applicationInterceptors
     *         additional application applicationInterceptors
     */
    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage, String appId,
            SocketFactory socketFactory, List<Interceptor> networkInterceptors,
            List<Interceptor> applicationInterceptors) {
        checkState(!Strings.isNullOrEmpty(baseUrl));
        checkState(!Strings.isNullOrEmpty(appId));
        checkNotNull(networkInterceptors);
        checkNotNull(applicationInterceptors);

        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.appId = appId;
        this.socketFactory = socketFactory;
        this.unauthenticatedRetrofit = getRetrofit(
                getHttpClientBuilder(
                        networkInterceptors,
                        applicationInterceptors)
                        .build());
        this.unauthenticatedServices = CacheBuilder.newBuilder()
                .build(new RetrofitServiceLoader(unauthenticatedRetrofit));
        authenticationApi = getClient(AuthenticationApi.class);

        this.networkInterceptors = ImmutableList.copyOf(networkInterceptors);
        this.applicationInterceptors = ImmutableList.copyOf(applicationInterceptors);
    }

    /**
     * Create an unauthenticated client (this client cannot authenticate automatically, and is only used for
     * public APIs not requiring a server user to access).
     *
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @return service client
     */
    public <T> T getClient(Class<T> service) {
        checkNotNull(service);

        //noinspection unchecked
        return (T) unauthenticatedServices.getUnchecked(service);
    }

    /**
     * Get an instance of the Authentication API client.
     * <p>
     * Basically a convenience wrapper for getClient(AuthenticationApi.class).
     *
     * @return authentication API
     */
    public AuthenticationApi getAuthenticationApi() {
        return authenticationApi;
    }


    /**
     * This provides a OkHttpClient.Builder to be used as a base for Bridge calls. Override to adjust OkHttpClient
     * properties. ApiClientProvider will add Bridge specific configurations.
     * <p>
     * Note that you should use the constructor to specify socket factory, interceptors, and network interceptors. Any
     * interceptors and network interceptors added in this method will be overwritten. If an Authenticator is set for
     * origin servers, it will also be overwritten.
     *
     * @return base http client builder
     */
    protected OkHttpClient.Builder getHttpClientBuilder() {
        // TODO Timeouts should be easily configurable https://sagebionetworks.jira.com/browse/BRIDGE-2332
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
    }

    OkHttpClient.Builder getHttpClientBuilder(List<Interceptor> networkInterceptors,
            List<Interceptor> applicationInterceptors) {
        OkHttpClient.Builder builder = getHttpClientBuilder();

        // reset these because we need to manage them in this class
        builder.networkInterceptors().clear();
        builder.interceptors().clear();
        builder.authenticator(Authenticator.NONE);

        if (socketFactory != null) {
            builder.socketFactory(socketFactory);
        }
        for (Interceptor interceptor : networkInterceptors) {
            builder.addNetworkInterceptor(interceptor);
        }
        for (Interceptor interceptor : applicationInterceptors) {
            builder.addInterceptor(interceptor);
        }
        return builder
                .addInterceptor(new HeaderInterceptor(userAgent, acceptLanguage))
                .addInterceptor(WARNING_INTERCEPTOR)
                .addInterceptor(ERROR_INTERCEPTOR)
                .addInterceptor(LOGGING_INTERCEPTOR);
    }

    Retrofit getRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(RestUtils.GSON))
                .build();
    }

    /**
     * Loader (basically a factory) for Retrofit service instances.
     */
    static class RetrofitServiceLoader extends CacheLoader<Class<?>, Object> {
        private final Retrofit retrofit;

        RetrofitServiceLoader(Retrofit retrofit) {
            this.retrofit = retrofit;
        }

        @SuppressWarnings("NullableProblems") // superclass uses nullity-analysis annotations which we are not using
        @Override
        public Object load(Class<?> serviceClass) {
            return retrofit.create(serviceClass);
        }
    }

    /**
     * Returns a builder for authenticated access to this app.
     *
     * @return builder for creating authenticated clients
     */
    public AuthenticatedClientProviderBuilder getAuthenticatedClientProviderBuilder() {
        return new AuthenticatedClientProviderBuilder();
    }

    /**
     * Base class for creating authenticated clients that are correctly configured to communicate with a Bridge study.
     */
    public class AuthenticatedClientProvider {

        private final UserSessionInfoProvider userSessionInfoProvider;
        private final LoadingCache<Class<?>, ?> authenticatedServices;

        private AuthenticatedClientProvider(final UserSessionInfoProvider userSessionInfoProvider,
                                            final Retrofit authenticatedRetrofit) {
            this.userSessionInfoProvider = userSessionInfoProvider;

            this.authenticatedServices = CacheBuilder.newBuilder()
                    .build(new RetrofitServiceLoader(authenticatedRetrofit));
        }

        // To build the ClientManager on this class, we need to have access to the session that is persisted
        // by the HTTP applicationInterceptors.
        public UserSessionInfoProvider getUserSessionInfoProvider() {
            return userSessionInfoProvider;
        }


        /**
         * @param <T>
         *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
         * @param service
         *         Class representing the service
         * @return service client that is authenticated with the user's credentials
         */
        public <T> T getClient(Class<T> service) {
            checkNotNull(service);

            //noinspection unchecked
            return (T) authenticatedServices.getUnchecked(service);
        }
    }

    /**
     * Builder for AuthenticatedClientProvider. Configure the builder with credentials for a user
     */
    public class AuthenticatedClientProviderBuilder {
        private Phone phone;
        private String email;
        private String externalId;
        private String password;
        private UserSessionInfo session;
        private final List<UserSessionInfoProvider.UserSessionInfoChangeListener> changeListeners = new ArrayList<>();

        private AuthenticatedClientProviderBuilder() {
        }

        /**
         * @param changeListener UserSessionInfo change listener
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder addUserSessionInfoChangeListener(
                UserSessionInfoProvider.UserSessionInfoChangeListener changeListener) {
            changeListeners.add(changeListener);
            return this;
        }

        /**
         * @param phone participant's phone
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder withPhone(Phone phone) {
            this.phone = phone;
            return this;
        }

        /**
         * @param email participant's email
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * @param externalId participant's externalId
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder withExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param password participant's password, if available
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * @param session participant's last active session, if available
         * @return this builder, for chaining operations
         */
        public AuthenticatedClientProviderBuilder withSession(UserSessionInfo session) {
            this.session = session;
            return this;
        }

        /**
         * Builds an AuthenticatedClientProvider. The credentials and/or session are cleared out when build() is called, and can
         * be
         * set again to build another AuthenticatedClientProvider for a different.
         *
         * @return instance of AuthenticatedClientProvider tied to a participant
         */
        public AuthenticatedClientProvider build() {
            checkState(!Strings.isNullOrEmpty(email) || phone != null || !Strings.isNullOrEmpty(externalId),
                    "requires email, phone or external ID");

            UserSessionInfoProvider sessionProvider =
                    new UserSessionInfoProvider(authenticationApi, appId, email, phone, externalId, password, session,
                            changeListeners);
            // reset credentials so same builder can be reused
            email = null;
            phone = null;
            externalId = null;

            password = null;
            session = null;

            UserSessionInterceptor sessionInterceptor = new UserSessionInterceptor(sessionProvider);
            AuthenticationHandler authenticationHandler = new AuthenticationHandler(sessionProvider);

            // put auth related interceptors first
            List<Interceptor> applicationInterceptorsWithAuth = Lists.newArrayList();
            applicationInterceptorsWithAuth.add(sessionInterceptor);
            applicationInterceptorsWithAuth.add(authenticationHandler);
            applicationInterceptorsWithAuth.addAll(applicationInterceptors);

            OkHttpClient.Builder httpClientBuilder = getHttpClientBuilder(networkInterceptors,
                    applicationInterceptorsWithAuth);
            httpClientBuilder.authenticator(authenticationHandler);

            Retrofit authenticatedRetrofit = getRetrofit(httpClientBuilder.build());

            return new AuthenticatedClientProvider(sessionProvider, authenticatedRetrofit);
        }
    }
}
