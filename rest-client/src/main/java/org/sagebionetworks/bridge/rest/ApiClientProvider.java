package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

/**
 * Base class for creating clients that are correctly configured to communicate with the
 * Bridge server.
 */
public class ApiClientProvider {

    private static final Interceptor WARNING_INTERCEPTOR = new WarningHeaderInterceptor();
    private static final Interceptor ERROR_INTERCEPTOR = new ErrorResponseInterceptor();
    private static final Interceptor LOGGING_INTERCEPTOR = new LoggingInterceptor();

    private final UserSessionInfoProvider userSessionInfoProvider;
    private final LoadingCache<Class<?>, ?> unauthenticatedServices;
    private final LoadingCache<Class<?>, ?> authenticatedServices;

    private ApiClientProvider(final UserSessionInfoProvider userSessionInfoProvider,
            final Retrofit unauthenticatedRetrofit, final Retrofit authenticatedRetrofit) {
        this.userSessionInfoProvider = userSessionInfoProvider;

        this.unauthenticatedServices = CacheBuilder.newBuilder()
                .build(new RetrofitServiceLoader(unauthenticatedRetrofit));

        this.authenticatedServices = CacheBuilder.newBuilder()
                .build(new RetrofitServiceLoader(authenticatedRetrofit));
    }

    private static class RetrofitServiceLoader extends CacheLoader<Class<?>, Object> {
        private final Retrofit retrofit;

        RetrofitServiceLoader(Retrofit retrofit) {
            this.retrofit = retrofit;
        }

        @SuppressWarnings("NullableProblems") // superclass uses nullity-analysis annotations which we are not using
        @Override public Object load(Class<?> serviceClass)  {
            return retrofit.create(serviceClass);
        }
    }

    // To build the ClientManager on this class, we need to have access to the session that is persisted
    // by the HTTP interceptors.
    public UserSessionInfoProvider getUserSessionInfoProvider() {
        return userSessionInfoProvider;
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
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @param signIn
     *         credentials for the user, or null for an unauthenticated client
     * @return service client that is authenticated with the user's credentials
     */
    public <T> T getClient(Class<T> service, SignIn signIn) {
        checkNotNull(service);
        checkNotNull(signIn);

        //noinspection unchecked
        return (T) authenticatedServices.getUnchecked(service);
    }

    public static class Builder {
        private final String baseUrl;
        private final String userAgent;
        private final String acceptLanguage;
        private final String study;
        private final Retrofit unauthenticatedRetrofit;
        private final AuthenticationApi authenticationApi;

        private String email;
        private String password;
        private UserSessionInfo session;

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
        public Builder(String baseUrl, String userAgent, String acceptLanguage, String study) {
            checkState(!Strings.isNullOrEmpty(baseUrl));
            checkState(!Strings.isNullOrEmpty(userAgent));
            checkState(!Strings.isNullOrEmpty(study));

            this.baseUrl = baseUrl;
            this.userAgent = userAgent;
            this.acceptLanguage = acceptLanguage;
            this.study = study;
            unauthenticatedRetrofit= getRetrofit(getHttpClientBuilder().build());
            authenticationApi = unauthenticatedRetrofit.create(AuthenticationApi.class);
        }

        public AuthenticationApi getAuthenticationApi() {
            return authenticationApi;
        }

        /**
         * @param email participant's email
         * @return this builder, for chaining operations
         */
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * @param password participant's password, if available
         * @return this builder, for chaining operations
         */
        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * @param session participant's last active session, if available
         * @return this builder, for chaining operations
         */
        public Builder withSession(UserSessionInfo session) {
            this.session = session;
            return this;
        }

        /**
         * Builds an ApiClientProvider. The credentials and/or session are cleared out when build() is called, and can
         * be
         * set again to build another ApiClientProvider for a different.
         *
         * @return instance of ApiClientProvider tied to a participant
         */
        public ApiClientProvider build() {
            checkState(!Strings.isNullOrEmpty(email), "email cannot be null or empty");
            checkState(!Strings.isNullOrEmpty(password) || session != null,
                    "requires at least one of password or session");

            UserSessionInfoProvider sessionProvider =
                    new UserSessionInfoProvider(authenticationApi, study, email, password, session);

            email = null;
            password = null;
            session = null;

            UserSessionInterceptor sessionInterceptor = new UserSessionInterceptor(sessionProvider);
            AuthenticationHandler authenticationHandler = new AuthenticationHandler(sessionProvider);

            OkHttpClient.Builder httpClientBuilder = getHttpClientBuilder(sessionInterceptor, authenticationHandler);
            httpClientBuilder.authenticator(authenticationHandler);

            Retrofit authenticatedRetrofit = getRetrofit(httpClientBuilder.build());

            return new ApiClientProvider(sessionProvider, unauthenticatedRetrofit, authenticatedRetrofit);
        }

        OkHttpClient.Builder getHttpClientBuilder(Interceptor... interceptors) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES);
            for (Interceptor interceptor : interceptors) {
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
    }
}
