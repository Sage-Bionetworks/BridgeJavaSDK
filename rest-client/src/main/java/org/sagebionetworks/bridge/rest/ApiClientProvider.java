package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
    private final String study;
    private final Retrofit unauthenticatedRetrofit;
    private final LoadingCache<Class<?>, ?> unauthenticatedServices;
    private final AuthenticationApi authenticationApi;
    private final ImmutableList<Interceptor> networkInterceptors;
    private final ImmutableList<Interceptor> applicationInterceptors;

    /**
     * Creates a builder for accessing services associated with an environment and study.
     *
     * @param baseUrl base url for Bridge service
     * @param userAgent
     *         user-agent string in Bridge's expected format, see {@link RestUtils#getUserAgent(ClientInfo)}
     * @param acceptLanguage
     *         optional comma-separated list of preferred languages for this client (most to least
     *         preferred
     * @param study
     *         study identifier
     */
    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage, String study) {
        this(baseUrl, userAgent, acceptLanguage, study, Collections.<Interceptor>emptyList(),
                Collections.<Interceptor>emptyList());
    }

    /**
     * Creates a builder for accessing services associated with an environment and study.
     *
     * @param baseUrl base url for Bridge service
     * @param userAgent
     *         user-agent string in Bridge's expected format, see {@link RestUtils#getUserAgent(ClientInfo)}
     * @param acceptLanguage
     *         optional comma-separated list of preferred languages for this client (most to least
     *         preferred
     * @param study
     *         study identifier
     * @param networkInterceptors
     *         additional network applicationInterceptors
     * @param applicationInterceptors
     *         additional application applicationInterceptors
     */
    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage, String study,
            List<Interceptor> networkInterceptors, List<Interceptor> applicationInterceptors) {
        checkState(!Strings.isNullOrEmpty(baseUrl));
        checkState(!Strings.isNullOrEmpty(userAgent));
        checkState(!Strings.isNullOrEmpty(study));
        checkNotNull(networkInterceptors);
        checkNotNull(applicationInterceptors);

        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.study = study;
        this.unauthenticatedRetrofit = getRetrofit(
                getHttpClientBuilder(
                        networkInterceptors,
                        applicationInterceptors)
                        .build());
        this.unauthenticatedServices = CacheBuilder.newBuilder()
                .build(new RetrofitServiceLoader(unauthenticatedRetrofit));
        authenticationApi = getClient(AuthenticationApi.class);

        this.networkInterceptors = ImmutableList.copyOf(networkInterceptors);
        this.applicationInterceptors = ImmutableList.copyOf(networkInterceptors);
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
     *
     * Basically a convenience wrapper for getClient(AuthenticationApi.class).
     * @return authentication API
     */
    public AuthenticationApi getAuthenticationApi() {
        return authenticationApi;
    }


    OkHttpClient.Builder getHttpClientBuilder(List<Interceptor> networkInterceptors,
            List<Interceptor> applicationInterceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES);
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
       @Override public Object load(Class<?> serviceClass)  {
           return retrofit.create(serviceClass);
       }
   }

    /**
     * Returns a builder for authenticated access to this study.
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
        private String password;
        private UserSessionInfo session;

        private AuthenticatedClientProviderBuilder() {
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
            checkState(email != null || phone != null, "requires either email or phone");

            UserSessionInfoProvider sessionProvider =
                    new UserSessionInfoProvider(authenticationApi, study, email, phone, password, session);

            // reset credentials so same builder can be reused
            email = null;
            phone = null;
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
